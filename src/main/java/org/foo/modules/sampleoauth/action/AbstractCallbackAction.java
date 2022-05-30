package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.TokenDataResultProcessor;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.JahiaAuthMapperService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
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
                logger.info("Token Data: {}", jahiaAuthMapperService.getMapperResultsForSession(httpServletRequest.getRequestedSessionId())
                        .get(TokenDataResultProcessor.MAPPER_NAME)
                        .get(JahiaOAuthConstants.TOKEN_DATA).getValue());
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
}
