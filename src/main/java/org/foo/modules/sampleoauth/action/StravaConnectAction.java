package org.foo.modules.sampleoauth.action;

import org.foo.modules.sampleoauth.connectors.StravaConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRCallback;
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

import javax.jcr.RepositoryException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(service = Action.class, immediate = true)
public class StravaConnectAction extends Action {
    private static final String NAME = "connectToStravaAction";

    private JahiaOAuthService jahiaOAuthService;
    private StravaConnectorImpl stravaConnectorImpl;

    @Reference(service = JahiaOAuthService.class)
    private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference(service = StravaConnectorImpl.class)
    private void refStravaConnectorImpl(StravaConnectorImpl stravaConnectorImpl) {
        this.stravaConnectorImpl = stravaConnectorImpl;
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
        String authorizationUrl = JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<String>() {
            @Override
            public String doInJCR(JCRSessionWrapper session) throws RepositoryException {
                JCRNodeWrapper jahiaOAuthNode = session.getNode(sitePath).getNode(JahiaOAuthConstants.JAHIA_OAUTH_NODE_NAME);
                return jahiaOAuthService.getAuthorizationUrl(jahiaOAuthNode, stravaConnectorImpl.getServiceName(), sessionId, new HashMap<String, String>());
            }
        });

        JSONObject response = new JSONObject();
        response.put(JahiaOAuthConstants.AUTHORIZATION_URL, authorizationUrl);
        return new ActionResult(HttpServletResponse.SC_OK, null, response);
    }
}
