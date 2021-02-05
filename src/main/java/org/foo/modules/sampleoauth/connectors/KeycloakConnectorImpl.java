package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.api.KeycloakApi;
import org.jahia.modules.jahiaoauth.service.ConnectorService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRCallback;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.*;

@Component(service = {KeycloakConnectorImpl.class, ConnectorService.class}, property = {JahiaOAuthConstants.CONNECTOR_SERVICE_NAME + "=" + KeycloakConnectorImpl.KEY}, immediate = true)
public class KeycloakConnectorImpl implements ConnectorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KeycloakConnectorImpl.class);
    public static final String KEY = "KeycloakApi20";
    public static final String REALM = "realm";
    public static final String BASEURL = "baseUrl";
    private static final String PROTECTED_RESOURCE_URL = "%s/auth/realms/%s/protocol/openid-connect/userinfo";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuth20Service(KEY, KeycloakApi.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuth20Service(KEY);
    }

    @Override
    public String getProtectedResourceUrl() {
        try {
            return JCRTemplate.getInstance().doExecuteWithSystemSession(new JCRCallback<String>() {
                @Override
                public String doInJCR(JCRSessionWrapper jcrSessionWrapper) throws RepositoryException {
                    String rootPath = "/sites/digitall/" + JahiaOAuthConstants.JAHIA_OAUTH_NODE_NAME + "/" + KEY;
                    if (!jcrSessionWrapper.nodeExists(rootPath)) {
                        return PROTECTED_RESOURCE_URL;
                    }
                    JCRNodeWrapper jcrNodeWrapper = jcrSessionWrapper.getNode(rootPath);
                    String baseUrl = jcrNodeWrapper.getPropertyAsString(BASEURL);
                    String realm = jcrNodeWrapper.getPropertyAsString(REALM);
                    return String.format(PROTECTED_RESOURCE_URL, baseUrl, realm);
                }
            });
        } catch (RepositoryException e) {
            LOGGER.error(e.getMessage(), e);
            return PROTECTED_RESOURCE_URL;
        }
    }

    @Override
    public List<Map<String, Object>> getAvailableProperties() {
        List<Map<String, Object>> availableProperties = new ArrayList<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "username");
        properties.put("propertyToRequest", "preferred_username");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put("name", "firstname");
        properties.put("propertyToRequest", "given_name");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put("name", "lastname");
        properties.put("propertyToRequest", "family_name");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        return Collections.unmodifiableList(availableProperties);
    }

    @Override
    public String getServiceName() {
        return KEY;
    }

    public void updateBaseUrlAndRealm(String baseUrl, String realm) {
        jahiaOAuthService.addOAuth20Service(KEY, KeycloakApi.instance(baseUrl, realm));
    }
}
