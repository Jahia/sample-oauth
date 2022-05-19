package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.foo.modules.sampleoauth.connectors.TokenDataResultProcessor;
import org.foo.modules.sampleoauth.utils.CustomLoginLogoutUrlProvider;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.JahiaAuthMapperService;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component(service = Action.class, immediate = true)
public class KeycloakCallbackAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakCallbackAction.class);

    private static final String NAME = "keycloakOAuthCallbackAction";

    private JahiaOAuthService jahiaOAuthService;
    private JahiaAuthMapperService jahiaAuthMapperService;
    private SettingsService settingsService;

    @Reference
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setJahiaAuthMapperService(JahiaAuthMapperService jahiaAuthMapperService) {
        this.jahiaAuthMapperService = jahiaAuthMapperService;
    }

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public KeycloakCallbackAction() {
        setName(NAME);
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
                jahiaOAuthService.extractAccessTokenAndExecuteMappers(settingsService.getConnectorConfig(renderContext.getSite().getSiteKey(), KeycloakConnectorImpl.KEY), token, httpServletRequest.getRequestedSessionId());
                logger.info("Token Data: {}", jahiaAuthMapperService.getMapperResultsForSession(httpServletRequest.getRequestedSessionId())
                        .get(TokenDataResultProcessor.MAPPER_NAME)
                        .get(JahiaOAuthConstants.TOKEN_DATA).getValue());
                String returnUrl = (String) httpServletRequest.getSession().getAttribute(CustomLoginLogoutUrlProvider.SESSION_REQUEST_URI);
                if (returnUrl == null || StringUtils.endsWith(returnUrl, "/start")) {
                    returnUrl = renderContext.getSite().getHome().getUrl();
                }
                // WARN: site query param is mandatory for the SSOValve in jahia-authentication module
                return new ActionResult(HttpServletResponse.SC_OK, returnUrl + "?site=", true, null);
            } catch (Exception e) {
                logger.error("", e);
            }
        } else {
            logger.error("Could not authenticate user with SSO, the callback from the server was missing mandatory parameters");
        }
        return ActionResult.BAD_REQUEST;
    }
}
