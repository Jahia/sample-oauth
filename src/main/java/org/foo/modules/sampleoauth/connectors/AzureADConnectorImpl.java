package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.builder.AzureConnectorBuilder;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.ConnectorPropertyInfo;
import org.jahia.modules.jahiaauth.service.ConnectorService;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.modules.jahiaoauth.service.OAuthConnectorService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.Arrays;
import java.util.List;

@Component(service = { AzureADConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class }, property = {
        JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + AzureADConnectorImpl.KEY }, immediate = true)
public class AzureADConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "AzureADApi20";

    private static final String PROTECTED_RESOURCE_URL = "https://login.microsoftonline.com/%s/v2.0/.well-known/openid-configuration";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, new AzureConnectorBuilder());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig config) {
        return "https://graph.microsoft.com/v1.0/me";
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(new ConnectorPropertyInfo("displayName", "string"), new ConnectorPropertyInfo("surname", "string"),
                new ConnectorPropertyInfo("userPrincipalName", "string"), new ConnectorPropertyInfo("givenName", "string"));
    }
}
