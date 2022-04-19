package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.foo.modules.sampleoauth.utils.CustomLoginLogoutUrlProvider;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.jahia.services.sites.JahiaSitesService;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(service = Action.class, immediate = true)
public class KeycloakConnectAction extends Action {
    private static final String NAME = "connectToKeycloakAction";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference(service = SettingsService.class)
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public KeycloakConnectAction() {
        setName(NAME);
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
                                  Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {
        String referer = httpServletRequest.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isNotBlank(referer)) {
            httpServletRequest.getSession(false).setAttribute(CustomLoginLogoutUrlProvider.SESSION_REQUEST_URI, referer);
        }

        JSONObject response = new JSONObject();
        response.put(JahiaOAuthConstants.AUTHORIZATION_URL,
                jahiaOAuthService.getAuthorizationUrl(settingsService.getConnectorConfig(JahiaSitesService.SYSTEM_SITE_KEY, KeycloakConnectorImpl.KEY),
                        httpServletRequest.getRequestedSessionId(), Collections.emptyMap()));
        return new ActionResult(HttpServletResponse.SC_OK, null, response);
    }
}
