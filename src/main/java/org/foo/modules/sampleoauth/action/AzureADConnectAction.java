package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.AzureADConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(service = Action.class)
public class AzureADConnectAction extends Action {
    private static final String NAME = "connectToAzureADAction";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;

    @Reference
    private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void refSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Activate
    public void onActivate() {
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
        setName(NAME);
    }

    @Override
    public ActionResult doExecute(HttpServletRequest req, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
                                  Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

        final String sessionId = req.getSession().getId();

        ConnectorConfig oauthConfig = settingsService.getConnectorConfig(renderContext.getSite().getSiteKey(), AzureADConnectorImpl.KEY);

        String authorizationUrl = jahiaOAuthService.getAuthorizationUrl(oauthConfig, sessionId, Collections.emptyMap());
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authorizationUrl);
        builder.replaceQueryParam("response_type", oauthConfig.getProperty("responseType"));
        if (StringUtils.isNotBlank(oauthConfig.getProperty("p"))) {
            builder.queryParam("p", oauthConfig.getProperty("p"));
        }
        builder.queryParam("nonce", oauthConfig.getProperty("nonce"));
        builder.queryParam("prompt", oauthConfig.getProperty("prompt"));
        builder.queryParam("response_mode", "form_post");
        JSONObject response = new JSONObject();
        response.put(JahiaOAuthConstants.AUTHORIZATION_URL, builder.build().toString());
        return new ActionResult(HttpServletResponse.SC_OK, null, response);
    }
}
