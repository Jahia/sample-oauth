package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.api.StravaApi20;
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

@Component(service = {StravaConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + StravaConnectorImpl.KEY}, immediate = true)
public class StravaConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "StravaApi20";
    private static final String PROTECTED_RESOURCE_URL = "https://www.strava.com/api/v3/athlete";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, StravaApi20.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig config) {
        return PROTECTED_RESOURCE_URL;
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(
                new ConnectorPropertyInfo("username", "string"),
                new ConnectorPropertyInfo("firstname", "string"),
                new ConnectorPropertyInfo("lastname", "string")
        );
    }

    @Override
    public void validateSettings(ConnectorConfig connectorConfig) {
        // Do nothing
    }
}
