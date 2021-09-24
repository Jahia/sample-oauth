package org.foo.modules.sampleoauth.valves;

import org.foo.modules.sampleoauth.mappers.OktaMapperImpl;
import org.jahia.api.Constants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthCacheService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.params.valves.*;
import org.jahia.pipelines.PipelineException;
import org.jahia.pipelines.valves.ValveContext;
import org.jahia.services.SpringContextSingleton;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.preferences.user.UserPreferencesHelper;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.settings.SettingsBean;
import org.jahia.utils.LanguageCodeConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.Map;

public class OktaOAuthValve extends AutoRegisteredBaseAuthValve {
    private static final Logger logger = LoggerFactory.getLogger(OktaOAuthValve.class);

    private JahiaUserManagerService jahiaUserManagerService;
    private JahiaOAuthService jahiaOAuthService;
    private JahiaOAuthCacheService jahiaOAuthCacheService;
    private CookieAuthConfig cookieAuthConfig;

    public void setJahiaUserManagerService(JahiaUserManagerService jahiaUserManagerService) {
        this.jahiaUserManagerService = jahiaUserManagerService;
    }

    public void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    public void setJahiaOAuthCacheService(JahiaOAuthCacheService jahiaOAuthCacheService) {
        this.jahiaOAuthCacheService = jahiaOAuthCacheService;
    }

    public void setCookieAuthConfig(CookieAuthConfig cookieAuthConfig) {
        this.cookieAuthConfig = cookieAuthConfig;
    }

    public static class LoginEvent extends BaseLoginEvent {
        private static final long serialVersionUID = 8966163034180261958L;

        public LoginEvent(Object source, JahiaUser jahiaUser, AuthValveContext authValveContext) {
            super(source, jahiaUser, authValveContext);
        }
    }

    @Override
    public void invoke(Object context, ValveContext valveContext) throws PipelineException {
        AuthValveContext authContext = (AuthValveContext) context;
        HttpServletRequest request = authContext.getRequest();

        if (authContext.getSessionFactory().getCurrentUser() != null) {
            valveContext.invokeNext(context);
            return;
        }

        String originalSessionId = request.getSession().getId();
        Map<String, Object> mapperResult;
        mapperResult = jahiaOAuthService.getMapperResults(OktaMapperImpl.MAPPER_SERVICE_NAME, originalSessionId);
        if (mapperResult == null || !request.getParameterMap().containsKey("site") || !mapperResult.containsKey("id")) {
            valveContext.invokeNext(context);
            return;
        }

        boolean ok = false;
        String siteKey = request.getParameter("site");
        String userId = (String) ((Map<String, Object>) mapperResult.get("id")).get(JahiaOAuthConstants.PROPERTY_VALUE);
        JCRUserNode userNode = jahiaUserManagerService.lookupUser(userId, siteKey);

        if (userNode != null) {
            if (!userNode.isAccountLocked()) {
                ok = true;
            } else {
                logger.warn("Login failed: account for user " + userNode.getName() + " is locked.");
                request.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.ACCOUNT_LOCKED);
            }
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug("Login failed. Unknown username " + userId);
            }
            request.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.UNKNOWN_USER);
        }

        if (ok) {
            if (logger.isDebugEnabled()) {
                logger.debug("User " + userNode + " logged in.");
            }

            JahiaUser jahiaUser = userNode.getJahiaUser();

            if (request.getSession(false) != null) {
                request.getSession().invalidate();
            }

            if (!originalSessionId.equals(request.getSession().getId())) {
                jahiaOAuthCacheService.updateCacheEntry(originalSessionId, request.getSession().getId());
            }

            request.setAttribute(LoginEngineAuthValveImpl.VALVE_RESULT, LoginEngineAuthValveImpl.OK);
            authContext.getSessionFactory().setCurrentUser(jahiaUser);

            // do a switch to the user's preferred language
            if (SettingsBean.getInstance().isConsiderPreferredLanguageAfterLogin()) {
                Locale preferredUserLocale = UserPreferencesHelper.getPreferredLocale(userNode, LanguageCodeConverters.resolveLocaleForGuest(request));
                request.getSession().setAttribute(Constants.SESSION_LOCALE, preferredUserLocale);
            }

            String useCookie = request.getParameter(LoginEngineAuthValveImpl.USE_COOKIE);
            if (("on".equals(useCookie))) {
                // the user has indicated he wants to use cookie authentication
                CookieAuthValveImpl.createAndSendCookie(authContext, userNode, cookieAuthConfig);
            }

            SpringContextSingleton.getInstance().publishEvent(new LoginEvent(this, jahiaUser, authContext));
        } else {
            valveContext.invokeNext(context);
        }
    }
}
