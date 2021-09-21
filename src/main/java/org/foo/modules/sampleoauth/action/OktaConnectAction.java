package org.foo.modules.sampleoauth.action;

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
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Action.class, immediate = true)
public class OktaConnectAction extends Action {
    private static final String NAME = "connectToOktaAction";

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
        final String sitePath = renderContext.getSite().getPath();
        final String sessionId = httpServletRequest.getSession().getId();
        String authorizationUrl = jcrTemplate.doExecuteWithSystemSession(systemSession -> {
            JCRNodeWrapper jahiaOAuthNode = systemSession.getNode(sitePath).getNode(JahiaOAuthConstants.JAHIA_OAUTH_NODE_NAME);
            JCRNodeWrapper jcrNodeWrapper = jahiaOAuthNode.getNode(OktaConnectorImpl.KEY);
            oktaConnectorImpl.registerService(jcrNodeWrapper.getPropertyAsString(OktaConnectorImpl.PROPERTY_ORGANIZATION));
            return jahiaOAuthService.getAuthorizationUrl(jahiaOAuthNode, oktaConnectorImpl.getServiceName(), sessionId, new HashMap<>());
        });

        JSONObject response = new JSONObject();
        response.put(JahiaOAuthConstants.AUTHORIZATION_URL, authorizationUrl);
        return new ActionResult(HttpServletResponse.SC_OK, null, response);
    }
}
