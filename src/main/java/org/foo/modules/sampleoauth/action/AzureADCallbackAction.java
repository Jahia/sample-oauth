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
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Component(service = Action.class)
public class AzureADCallbackAction extends Action {
    private static final Logger logger = LoggerFactory.getLogger(AzureADCallbackAction.class);

    private static final String NAME = "azureADOAuthCallbackAction";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;

    @Reference(service = JahiaOAuthService.class)
    private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference(service = SettingsService.class)
    private void refSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public AzureADCallbackAction() {
        setName(NAME);
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
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
                isAuthenticate = true;
            } catch (Exception ex) {
                logger.error("Could not authenticate user", ex);
            }
        } else {
            logger.error("Could not authenticate user with Google, the callback from the Google server was missing mandatory parameters");
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
