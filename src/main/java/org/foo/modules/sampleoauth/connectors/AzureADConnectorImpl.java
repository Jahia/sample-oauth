package org.foo.modules.sampleoauth.connectors;

import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api;
import org.jahia.modules.jahiaauth.service.*;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.modules.jahiaoauth.service.OAuthConnectorService;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component(service = {AzureADConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + AzureADConnectorImpl.KEY}, immediate = true)
public class AzureADConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "AzureADApi20";
    private static final String TENANT_ID = "tenantID";
    private ConfigurationAdmin configurationAdmin;
    private SettingsService settingsService;
    private static Logger logger = LoggerFactory.getLogger(AzureADConnectorImpl.class);

    private static final String PROTECTED_RESOURCE_URL = "https://login.microsoftonline.com/%s/v2.0/.well-known/openid-configuration";
   // private static final String PROTECTED_RESOURCE_URL = "https://login.microsoftonline.com/%s/oauth2/v2.0/authorize";

    @Reference(service = SettingsService.class)
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }


    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }


    @Reference(service = ConfigurationAdmin.class, name = "configurationAdmin")
    private void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }


    @Activate
    private void onActivate() {
        Configuration[] configurations = null;
        try {
            configurations = this.configurationAdmin.listConfigurations("(service.factoryPid=org.jahia.modules.auth)");
        } catch (IOException e) {
            logger.error("IOException reading org.jahia.modules.auth configs", e);
        } catch (InvalidSyntaxException e) {
            logger.error("InvalidSyntaxException reading org.jahia.modules.auth configs", e);
        }

        if (configurations != null) {
            int length = configurations.length;
            for(int i = 0; i < length; ++i) {
                Configuration configuration = configurations[i];
                ConnectorConfig config = settingsService.getConnectorConfig((String)configuration.getProperties().get("siteKey"), KEY);
                if(config != null) {
                    this.validateSettings(config);
                }
            }
        }
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig config) {
        return "https://graph.microsoft.com/v1.0/me";
        //return String.format(PROTECTED_RESOURCE_URL, config.getProperty(TENANT_ID));
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(
                new ConnectorPropertyInfo("displayName", "string"),
                new ConnectorPropertyInfo("surname", "string"),
                new ConnectorPropertyInfo("userPrincipalName", "string"),
                new ConnectorPropertyInfo("givenName", "string")
        );
    }

    @Override
    public void validateSettings(ConnectorConfig connectorConfig) {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, MicrosoftAzureActiveDirectory20Api.custom(connectorConfig.getProperty(TENANT_ID)));
    }
}
