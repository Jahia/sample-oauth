package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.api.BitbucketApi20;
import org.foo.modules.sampleoauth.api.GitlabApi20;
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

import java.util.Collections;
import java.util.List;

@Component(service = {BitbucketConnectorImpl.class, OAuthConnectorService.class, ConnectorService.class}, property = {JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + BitbucketConnectorImpl.KEY}, immediate = true)
public class BitbucketConnectorImpl implements OAuthConnectorService {
    public static final String KEY = "BitbucketApi20";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuthDefaultApi20(KEY, BitbucketApi20.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuthDefaultApi20(KEY);
    }

    @Override
    public String getProtectedResourceUrl(ConnectorConfig connectorConfig) {
        return "https://api.bitbucket.org/2.0/user";
    }

    @Override
    public List<ConnectorPropertyInfo> getAvailableProperties() {
        return Collections.singletonList(new ConnectorPropertyInfo("account_id", "string"));
    }
}
