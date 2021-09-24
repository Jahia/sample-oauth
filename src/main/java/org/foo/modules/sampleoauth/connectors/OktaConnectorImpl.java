package org.foo.modules.sampleoauth.connectors;

import org.foo.modules.sampleoauth.api.OktaApi20;
import org.jahia.modules.jahiaoauth.service.ConnectorService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.content.JCRNodeWrapper;
import org.jahia.services.content.JCRTemplate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import java.util.*;

@Component(service = {OktaConnectorImpl.class, ConnectorService.class}, property = {JahiaOAuthConstants.CONNECTOR_SERVICE_NAME + "=" + OktaConnectorImpl.KEY}, immediate = true)
public class OktaConnectorImpl implements ConnectorService {
    private static final Logger logger = LoggerFactory.getLogger(OktaConnectorImpl.class);

    public static final String KEY = "OktaApi20";
    private static final String NODETYPE = "soauthnt:oktaOAuthSettings";
    public static final String PROPERTY_ORGANIZATION = "organization";
    private static final String DEFAULT_ORGANIZATION = "localhost";
    private static final String PROTECTED_RESOURCE_URL = "https://%s/oauth2/v1/userinfo";

    private JCRTemplate jcrTemplate;
    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JCRTemplate.class)
    private void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    public void registerService(String organization) {
        jahiaOAuthService.addOAuth20Service(KEY, OktaApi20.instance(organization));
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuth20Service(KEY);
    }

    @Override
    public String getServiceName() {
        return KEY;
    }

    @Override
    public String getProtectedResourceUrl() {
        try {
            return jcrTemplate.doExecuteWithSystemSession(jcrSessionWrapper -> {
                JCRNodeIteratorWrapper it = jcrSessionWrapper.getWorkspace().getQueryManager().createQuery(
                        String.format("SELECT * FROM [%s]", NODETYPE), Query.JCR_SQL2
                ).execute().getNodes();
                if (it.hasNext()) {
                    return String.format(PROTECTED_RESOURCE_URL, ((JCRNodeWrapper) it.nextNode()).getPropertyAsString(PROPERTY_ORGANIZATION));
                }
                return String.format(PROTECTED_RESOURCE_URL, DEFAULT_ORGANIZATION);
            });
        } catch (RepositoryException e) {
            logger.error(e.getMessage(), e);
            return String.format(PROTECTED_RESOURCE_URL, DEFAULT_ORGANIZATION);
        }
    }

    @Override
    public List<Map<String, Object>> getAvailableProperties() {
        List<Map<String, Object>> availableProperties = new ArrayList<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put(JahiaOAuthConstants.PROPERTY_NAME, "id");
        properties.put(JahiaOAuthConstants.PROPERTY_TO_REQUEST, "idtechnique");
        properties.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        properties.put(JahiaOAuthConstants.CAN_BE_REQUESTED, true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put(JahiaOAuthConstants.PROPERTY_NAME, "firstname");
        properties.put(JahiaOAuthConstants.PROPERTY_TO_REQUEST, "given_name");
        properties.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        properties.put(JahiaOAuthConstants.CAN_BE_REQUESTED, true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put(JahiaOAuthConstants.PROPERTY_NAME, "lastname");
        properties.put(JahiaOAuthConstants.PROPERTY_TO_REQUEST, "family_name");
        properties.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        properties.put(JahiaOAuthConstants.CAN_BE_REQUESTED, true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put(JahiaOAuthConstants.PROPERTY_NAME, "email");
        properties.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "email");
        properties.put(JahiaOAuthConstants.CAN_BE_REQUESTED, true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put(JahiaOAuthConstants.PROPERTY_NAME, "groups");
        properties.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        properties.put(JahiaOAuthConstants.CAN_BE_REQUESTED, true);
        availableProperties.add(properties);
        return Collections.unmodifiableList(availableProperties);
    }
}
