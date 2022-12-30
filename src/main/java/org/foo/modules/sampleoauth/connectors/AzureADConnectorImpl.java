package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.api.AzureB2CApi;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.ConnectorPropertyInfo;
import org.jahia.modules.jahiaauth.service.ConnectorService;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.modules.jahiaoauth.service.OAuthConnectorService;
import org.jahia.services.sites.JahiaSitesService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component(service = {AzureADConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {
        JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + AzureADConnectorImpl.KEY}, immediate = true)
public class AzureADConnectorImpl implements OAuthConnectorService {
    private static final Logger logger = LoggerFactory.getLogger(AzureADConnectorImpl.class);
    public static final String KEY = "AzureADApi20";

    private JahiaOAuthService jahiaOAuthService;
    private JahiaSitesService jahiaSitesService;
    private SettingsService settingsService;

    @Reference
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setJahiaSitesService(JahiaSitesService jahiaSitesService) {
        this.jahiaSitesService = jahiaSitesService;
    }

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, connectorConfig -> AzureB2CApi.instance(connectorConfig.getProperty("host"), connectorConfig.getProperty("tenantID")));
        jahiaSitesService.getSitesNames().forEach(siteName -> {
            ConnectorConfig connectorConfig = settingsService.getConnectorConfig(siteName, KEY);
            if (connectorConfig != null) {
                try {
                    validateSettings(connectorConfig);
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        });
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
