package org.foo.modules.sampleoauth.connectors;

import com.github.scribejava.apis.SalesforceApi;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;

@Component(service = { SalesforceConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + SalesforceConnectorImpl.KEY}, immediate = true)
public class SalesforceConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "SalesforceApi";
    private static final String SANDBOX = "sandbox";
    private static final String PRODUCTION_HOST_USER_INFO = "https://login.salesforce.com/services/oauth2/userinfo";
    private static final String SANDBOX_HOST_USER_INFO = "https://test.salesforce.com/services/oauth2/userinfo";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY,  SalesforceApi.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig config) {
        if (config.getProperty(SANDBOX).contains("true")){
            return SANDBOX_HOST_USER_INFO;
        }else{
            return PRODUCTION_HOST_USER_INFO;
        }
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Arrays.asList(
                new ConnectorPropertyInfo("sub", "string"),
                new ConnectorPropertyInfo("preferred_username", "string"),
                new ConnectorPropertyInfo("photos", "string"),
                new ConnectorPropertyInfo("user_type", "string"),
                new ConnectorPropertyInfo("nickname", "string"),
                new ConnectorPropertyInfo("email", "email"),
                new ConnectorPropertyInfo("email_verified", "string"),
                new ConnectorPropertyInfo("address", "string"),
                new ConnectorPropertyInfo("given_name", "string"),
                new ConnectorPropertyInfo("user_id", "string"),
                new ConnectorPropertyInfo("organization_id", "string"),
                new ConnectorPropertyInfo("name", "string"),
                new ConnectorPropertyInfo("phone_number", "string"),
                new ConnectorPropertyInfo("family_name", "string"),
                new ConnectorPropertyInfo("zoneinfo", "string")
        );
    }

    @Override
    public void validateSettings(ConnectorConfig connectorConfig) {
        if (connectorConfig.getProperty(SANDBOX).contains("true")){
            jahiaOAuthService.addOAuthDefaultApi20(KEY,  SalesforceApi.sandbox());
        }
    }
}
