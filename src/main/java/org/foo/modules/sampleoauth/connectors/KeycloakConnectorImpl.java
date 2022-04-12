package org.foo.modules.sampleoauth.connectors;

import com.github.scribejava.apis.KeycloakApi;
import org.jahia.modules.jahiaauth.service.*;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.modules.jahiaoauth.service.OAuthConnectorService;
import org.jahia.services.sites.JahiaSitesService;
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
import java.util.Objects;

@Component(service = {KeycloakConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + KeycloakConnectorImpl.KEY}, immediate = true)
public class KeycloakConnectorImpl implements OAuthConnectorService {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakConnectorImpl.class);

    public static final String KEY = "KeycloakApi20";
    public static final String REALM = "realm";
    public static final String BASEURL = "baseUrl";
    private static final String PROTECTED_RESOURCE_URL = "%s/auth/realms/%s/protocol/openid-connect/userinfo";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;
    private ConfigurationAdmin configurationAdmin;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Reference(service = ConfigurationAdmin.class, name = "configurationAdmin")
    private void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, KeycloakApi.instance());
        try {
            Configuration[] configurations = this.configurationAdmin.listConfigurations("(service.factoryPid=org.jahia.modules.auth)");
            if (configurations != null) {
                Arrays.stream(configurations)
                        .map(configuration -> settingsService.getConnectorConfig(JahiaSitesService.SYSTEM_SITE_KEY, KEY))
                        .filter(Objects::nonNull).findAny()
                        .ifPresent(this::validateSettings);
            }
        } catch (IOException | InvalidSyntaxException e) {
            getError(e);
        }
    }

    private void getError(Exception e) {
        logger.error("", e);
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig connectorConfig) {
        return String.format(PROTECTED_RESOURCE_URL, connectorConfig.getProperty(BASEURL), connectorConfig.getProperty(REALM));
    }

    private static ConnectorPropertyInfo getUserInfo(String name, String finalName) {
        ConnectorPropertyInfo connectorPropertyInfo = new ConnectorPropertyInfo(name, "string");
        connectorPropertyInfo.setPropertyToRequest(finalName);
        return connectorPropertyInfo;
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(
                getUserInfo("username", "preferred_username"),
                getUserInfo("firstname", "given_name"),
                getUserInfo("lastname", "family_name"),
                new ConnectorPropertyInfo(JahiaOAuthConstants.TOKEN_DATA, "string"),
                new ConnectorPropertyInfo("group", "string")
        );
    }

    @Override
    public void validateSettings(ConnectorConfig connectorConfig) {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, KeycloakApi.instance(connectorConfig.getProperty(BASEURL), connectorConfig.getProperty(REALM)));
    }
}
