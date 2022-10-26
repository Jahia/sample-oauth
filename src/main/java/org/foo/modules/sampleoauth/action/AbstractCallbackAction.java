package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.TokenDataResultProcessor;
import org.jahia.api.Constants;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaauth.service.JahiaAuthMapperService;
import org.jahia.modules.jahiaauth.service.MappedProperty;
import org.jahia.osgi.BundleUtils;
import org.jahia.params.valves.AuthValveContext;
import org.jahia.params.valves.BaseLoginEvent;
import org.jahia.params.valves.LoginEngineAuthValveImpl;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.JCRSessionFactory;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.preferences.user.UserPreferencesHelper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.settings.SettingsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public abstract class AbstractCallbackAction extends AbstractOAuthAction {
    private static final Logger logger = LoggerFactory.getLogger(AbstractCallbackAction.class);

    private JahiaAuthMapperService jahiaAuthMapperService;

    public void setJahiaAuthMapperService(JahiaAuthMapperService jahiaAuthMapperService) {
        this.jahiaAuthMapperService = jahiaAuthMapperService;
    }

    protected abstract String getActionName();

    protected abstract String getConnectorService();

    public AbstractCallbackAction() {
        setName(getActionName());
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource,
                                  JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> parameters, URLResolver urlResolver) {
        if (parameters.containsKey("code")) {
            final String token = getRequiredParameter(parameters, "code");
            if (StringUtils.isBlank(token)) {
                return ActionResult.BAD_REQUEST;
            }

            try {
                String siteKey = renderContext.getSite().getSiteKey();
                getJahiaOAuthService().extractAccessTokenAndExecuteMappers(getSettingsService().getConnectorConfig(siteKey, getConnectorService()), token, httpServletRequest.getRequestedSessionId());
                authenticateUser(renderContext);
                String returnUrl = (String) httpServletRequest.getSession().getAttribute(SESSION_REQUEST_URI);
                if (returnUrl == null || StringUtils.endsWith(returnUrl, "/start")) {
                    returnUrl = renderContext.getSite().getHome().getUrl();
                }
                // WARN: site query param is mandatory for the SSOValve in jahia-authentication module
                return new ActionResult(HttpServletResponse.SC_OK, returnUrl + "?site=" + siteKey, true, null);
            } catch (Exception e) {
                logger.error("", e);
            }
        } else {
            logger.error("Could not authenticate user with SSO, the callback from the server was missing mandatory parameters");
        }
        return ActionResult.BAD_REQUEST;
    }

    private void authenticateUser(RenderContext renderContext) {
        HttpServletRequest httpServletRequest = renderContext.getRequest();
        AuthValveContext authContext = new AuthValveContext(httpServletRequest, renderContext.getResponse(), JCRSessionFactory.getInstance());

        String originalSessionId = httpServletRequest.getSession(false).getId();
        logger.debug("JahiaOAuthService invoke pour l'originalSessionId : " + originalSessionId);

        JahiaAuthMapperService jahiaAuthMapperService = BundleUtils.getOsgiService(JahiaAuthMapperService.class, null);
        Map<String, MappedProperty> mapperResult = jahiaAuthMapperService.getMapperResultsForSession(originalSessionId).get(TokenDataResultProcessor.MAPPER_NAME);
        if (mapperResult != null) {
            JCRUserNode jcrUserNode = JahiaUserManagerService.getInstance().lookupUser((String) mapperResult.get(JahiaAuthConstants.SSO_LOGIN).getValue(),
                    renderContext.getSite().getSiteKey());
            if (!jcrUserNode.isAccountLocked()) {
                logger.debug("Compte non verrouillé");

                JahiaUser jahiaUser = jcrUserNode.getJahiaUser();

                if (httpServletRequest.getSession(false) != null) {
                    httpServletRequest.getSession().invalidate();
                }

                if (!originalSessionId.equals(httpServletRequest.getSession().getId())) {
                    logger.debug("Changement d'id de session {} -> {}", originalSessionId, httpServletRequest.getSession().getId());
                    jahiaAuthMapperService.updateCacheEntry(originalSessionId, httpServletRequest.getSession().getId());
                }

                httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, "ok");
                authContext.getSessionFactory().setCurrentUser(jahiaUser);

                httpServletRequest.getSession().setAttribute(Constants.SESSION_USER, jahiaUser);

                // do a switch to the user's preferred language
                if (SettingsBean.getInstance().isConsiderPreferredLanguageAfterLogin()) {
                    Locale preferredUserLocale = UserPreferencesHelper.getPreferredLocale(jcrUserNode, Locale.FRANCE);
                    httpServletRequest.getSession().setAttribute(Constants.SESSION_LOCALE, preferredUserLocale);
                }

                SpringContextSingleton.getInstance().publishEvent(new LoginEvent(this, jahiaUser, authContext));
            } else {
                logger.warn("Login failed: account for user " + jcrUserNode.getName() + " is locked.");
                httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, "account_locked");
            }
        } else {
            logger.warn("Login failed. Unknown username.");
            httpServletRequest.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, "unknown_user");
        }
    }

    public static class LoginEvent extends BaseLoginEvent {
        private static final long serialVersionUID = 8966163034180261951L;

        public LoginEvent(final Object source, final JahiaUser jahiaUser, final AuthValveContext authValveContext) {
            super(source, jahiaUser, authValveContext);
        }
    }
}
