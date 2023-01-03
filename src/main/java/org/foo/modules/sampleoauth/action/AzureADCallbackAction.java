package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.AzureADConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.bin.ActionResult;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaauth.service.JahiaAuthMapperService;
import org.jahia.modules.jahiaauth.service.MappedProperty;
import org.jahia.modules.jahiaauth.service.MappedPropertyInfo;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(service = Action.class)
public class AzureADCallbackAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(AzureADCallbackAction.class);

    private static final String NAME = "azureADOAuthCallbackAction";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;
    private JahiaAuthMapperService jahiaAuthMapperService;

    @Reference
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Reference
    private void setJahiaAuthMapperService(JahiaAuthMapperService jahiaAuthMapperService) {
        this.jahiaAuthMapperService = jahiaAuthMapperService;
    }

    public AzureADCallbackAction() {
        setName(NAME);
        setRequireAuthenticatedUser(false);
        setRequiredMethods("GET,POST");
    }

    @Override
    public ActionResult doExecute(final HttpServletRequest req, RenderContext renderContext, Resource resource,
                                  final JCRSessionWrapper session, Map<String, List<String>> parameters, URLResolver urlResolver) throws Exception {

        Boolean isAuthenticate = false;
        if (parameters.containsKey("code") && parameters.containsKey(JahiaOAuthConstants.STATE)) {
            final String token = parameters.get("code").get(0);
            final String state = parameters.get(JahiaOAuthConstants.STATE).get(0);
            if (StringUtils.isBlank(token) || StringUtils.isBlank(state)) {
                return ActionResult.BAD_REQUEST;
            }
            String siteKey = renderContext.getSite().getSiteKey();
            ConnectorConfig oauthConfig = settingsService.getConnectorConfig(siteKey, AzureADConnectorImpl.KEY);
            try {
                jahiaOAuthService.extractAccessTokenAndExecuteMappers(oauthConfig, token, state);
                if (parameters.containsKey("id_token")) {
                    JSONObject jwt = new JSONObject(new String(Base64.getDecoder().decode(parameters.get("id_token").get(0).split("\\.")[1].getBytes(StandardCharsets.UTF_8))));
                    if (jwt.has("name")) {
                        jahiaAuthMapperService.cacheMapperResults(AzureADConnectorImpl.KEY, RequestContextHolder.getRequestAttributes().getSessionId(),
                                Collections.singletonMap(JahiaAuthConstants.SSO_LOGIN, new MappedProperty(
                                        new MappedPropertyInfo(JahiaAuthConstants.SSO_LOGIN), jwt.getString("name"))));
                    }
                }

                isAuthenticate = true;
            } catch (Exception ex) {
                logger.error("Could not authenticate user", ex);
            }
        } else {
            logger.error("Could not authenticate user with Azure, the callback from the Azure server was missing mandatory parameters: {}", parameters);
        }

        final String redirectUrl;
        if (parameters.containsKey("redirect") && !parameters.get("redirect").isEmpty() && isAuthenticate) {
            redirectUrl = jahiaOAuthService.getResultUrl(renderContext.getSite().getUrl(), isAuthenticate) + "&redirect=" + parameters.get("redirect").get(0);
        } else {
            redirectUrl = jahiaOAuthService.getResultUrl(renderContext.getSite().getUrl(), isAuthenticate);
        }

        return new ActionResult(HttpServletResponse.SC_OK, redirectUrl, true, null);
    }
}
