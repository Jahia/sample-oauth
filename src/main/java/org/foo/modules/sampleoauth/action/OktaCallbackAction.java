package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.OktaConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component(service = Action.class, immediate = true)
public class OktaCallbackAction extends Action {
    private static final Logger LOGGER = LoggerFactory.getLogger(OktaCallbackAction.class);
    private static final String NAME = "oktaOAuthCallbackAction";

    private JCRTemplate jcrTemplate;
    private JahiaOAuthService jahiaOAuthService;
    private OktaConnectorImpl oktaConnectorImpl;

    @Reference(service = JCRTemplate.class)
    private void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference(service = OktaConnectorImpl.class)
    private void setOktaConnectorImpl(OktaConnectorImpl oktaConnectorImpl) {
        this.oktaConnectorImpl = oktaConnectorImpl;
    }

    @Activate
    public void onActivate() {
        setRequireAuthenticatedUser(false);
        setName(NAME);
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper jcrSessionWrapper, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        Boolean isAuthenticate = false;
        if (parameters.containsKey("code") && parameters.containsKey(JahiaOAuthConstants.STATE)) {
            final String token = parameters.get("code").get(0);
            final String state = parameters.get(JahiaOAuthConstants.STATE).get(0);
            if (StringUtils.isBlank(token) || StringUtils.isBlank(state)) {
                return ActionResult.BAD_REQUEST;
            }

            final String sitePath = renderContext.getSite().getPath();
            isAuthenticate = jcrTemplate.doExecuteWithSystemSession(systemSession -> {
                JCRNodeWrapper jahiaOAuthNode = systemSession.getNode(sitePath).getNode(JahiaOAuthConstants.JAHIA_OAUTH_NODE_NAME);
                try {
                    jahiaOAuthService.extractAccessTokenAndExecuteMappers(jahiaOAuthNode, oktaConnectorImpl.getServiceName(), token, state);
                    return true;
                } catch (Exception ex) {
                    LOGGER.error("Could not authenticate user", ex);
                    return false;
                }
            });
        } else {
            LOGGER.error("Could not authenticate user with Okta, the callback from the Okta server was missing mandatory parameters");
        }

        return new ActionResult(HttpServletResponse.SC_OK,
                jahiaOAuthService.getResultUrl(renderContext.getSite().getUrl(), isAuthenticate),
                true, null);
    }
}
