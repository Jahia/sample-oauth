package org.foo.modules.sampleoauth.connectors;

import com.github.scribejava.apis.LinkedInApi20;
import org.jahia.modules.jahiaoauth.service.ConnectorService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.util.*;

@Component(service = {LinkedinConnectorImpl.class, ConnectorService.class}, property = {JahiaOAuthConstants.CONNECTOR_SERVICE_NAME + "=" + LinkedinConnectorImpl.KEY}, immediate = true)
public class LinkedinConnectorImpl implements ConnectorService {
    public static final String KEY = "LinkedinApi20";
    private static final String PROTECTED_RESOURCE_URL = "https://api.linkedin.com/v2/me";

    private JahiaOAuthService jahiaOAuthService;

    @Reference(service = JahiaOAuthService.class)
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Activate
    private void onActivate() {
        jahiaOAuthService.addOAuth20Service(KEY, LinkedInApi20.instance());
    }

    @Deactivate
    private void onDeactivate() {
        jahiaOAuthService.removeOAuth20Service(KEY);
    }

    @Override
    public String getProtectedResourceUrl() {
        return PROTECTED_RESOURCE_URL;
    }

    @Override
    public List<Map<String, Object>> getAvailableProperties() {
        List<Map<String, Object>> availableProperties = new ArrayList<>();
        Map<String, Object> properties = new HashMap<>();
        properties.put("name", "id");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put("name", "firstname");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        properties = new HashMap<>();
        properties.put("name", "lastname");
        properties.put("valueType", "string");
        properties.put("canBeRequested", true);
        availableProperties.add(properties);
        return Collections.unmodifiableList(availableProperties);
    }

    @Override
    public String getServiceName() {
        return KEY;
    }
}
