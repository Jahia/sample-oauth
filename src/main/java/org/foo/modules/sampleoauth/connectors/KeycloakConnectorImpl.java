package org.foo.modules.sampleoauth.connectors;

import com.github.scribejava.apis.KeycloakApi;
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
import java.util.Collections;
import java.util.List;

@Component(service = {KeycloakConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + KeycloakConnectorImpl.KEY}, immediate = true)
public class KeycloakConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "KeycloakApi20";
    private static final String REALM = "realm";
    private static final String BASEURL = "baseUrl";
    private static final String PROTECTED_RESOURCE_URL = "%s/auth/realms/%s/protocol/openid-connect/userinfo";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, KeycloakApi.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig connectorConfig) {
        return String.format(PROTECTED_RESOURCE_URL, connectorConfig.getProperty(BASEURL), connectorConfig.getProperty(REALM));
    }

    private static ConnectorPropertyInfo getUserInfo(String name, String valueType, String finalName) {
        ConnectorPropertyInfo connectorPropertyInfo = new ConnectorPropertyInfo(name, valueType);
        connectorPropertyInfo.setPropertyToRequest(finalName);
        return connectorPropertyInfo;
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(
                getUserInfo("username", "string", "preferred_username"),
                getUserInfo("firstname", "string", "given_name"),
                getUserInfo("lastname", "string", "family_name")
        );
    }

    @Override
    public void validateSettings(ConnectorConfig connectorConfig) {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, KeycloakApi.instance(connectorConfig.getProperty(BASEURL), connectorConfig.getProperty(REALM)));
    }
}
