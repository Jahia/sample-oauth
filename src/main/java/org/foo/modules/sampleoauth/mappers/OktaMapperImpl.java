package org.foo.modules.sampleoauth.mappers;

import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.modules.jahiaoauth.service.MapperService;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRGroupNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.services.usermanager.JahiaUserManagerService;
import org.jahia.taglibs.user.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.*;

@Component(service = {OktaMapperImpl.class, MapperService.class}, property = {JahiaOAuthConstants.MAPPER_SERVICE_NAME + "=" + OktaMapperImpl.MAPPER_SERVICE_NAME}, immediate = true)
public class OktaMapperImpl implements MapperService {
    private static final Logger logger = LoggerFactory.getLogger(OktaMapperImpl.class);
    public static final String MAPPER_SERVICE_NAME = "oktaOAuthMapper";

    private JCRTemplate jcrTemplate;
    private JahiaUserManagerService jahiaUserManagerService;
    private JahiaGroupManagerService jahiaGroupManagerService;

    @Reference(service = JCRTemplate.class)
    public void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Reference(service = JahiaUserManagerService.class)
    public void setJahiaUserManagerService(JahiaUserManagerService jahiaUserManagerService) {
        this.jahiaUserManagerService = jahiaUserManagerService;
    }

    @Reference(service = JahiaGroupManagerService.class)
    public void setJahiaGroupManagerService(JahiaGroupManagerService jahiaGroupManagerService) {
        this.jahiaGroupManagerService = jahiaGroupManagerService;
    }

    @Override
    public List<Map<String, Object>> getProperties() {
        List<Map<String, Object>> properties = new ArrayList<>();
        Map<String, Object> property = new HashMap<>();
        property.put(JahiaOAuthConstants.PROPERTY_NAME, "id");
        property.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        property.put(JahiaOAuthConstants.PROPERTY_MANDATORY, false);
        properties.add(property);
        property = new HashMap<>();
        property.put(JahiaOAuthConstants.PROPERTY_NAME, "firstname");
        property.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        property.put(JahiaOAuthConstants.PROPERTY_MANDATORY, false);
        properties.add(property);
        property = new HashMap<>();
        property.put(JahiaOAuthConstants.PROPERTY_NAME, "lastname");
        property.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        property.put(JahiaOAuthConstants.PROPERTY_MANDATORY, false);
        properties.add(property);
        property = new HashMap<>();
        property.put(JahiaOAuthConstants.PROPERTY_NAME, "email");
        property.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "email");
        property.put(JahiaOAuthConstants.PROPERTY_MANDATORY, false);
        properties.add(property);
        property = new HashMap<>();
        property.put(JahiaOAuthConstants.PROPERTY_NAME, "groups");
        property.put(JahiaOAuthConstants.PROPERTY_VALUE_TYPE, "string");
        property.put(JahiaOAuthConstants.PROPERTY_MANDATORY, false);
        properties.add(property);
        return properties;
    }

    @Override
    public void executeMapper(Map<String, Object> mapperResult) {
        try {
            jcrTemplate.doExecuteWithSystemSession(systemSession -> {
                if (mapperResult.containsKey("id")) {
                    String userId = (String) ((Map<String, Object>) mapperResult.get("id")).get(JahiaOAuthConstants.PROPERTY_VALUE);
                    JCRUserNode userNode = jahiaUserManagerService.lookupUser(userId, systemSession);
                    if (userNode == null) {
                        userNode = jahiaUserManagerService.createUser(userId, "SHA-1:*", new Properties(), systemSession);
                        if (userNode == null) {
                            throw new RuntimeException("Cannot create user from access token");
                        }
                        userNode.setProperty("preferredLanguage", "fr");
                    } else {
                        removeUserMembership(userNode);
                    }
                    updateUserProperty(userNode, "firstname", "j:firstName", mapperResult);
                    updateUserProperty(userNode, "lastname", "j:lastName", mapperResult);
                    updateUserProperty(userNode, "email", "j:email", mapperResult);

                    if (mapperResult.containsKey("groups")) {
                        manageUserGroups(userNode, (JSONArray) ((Map<String, Object>) mapperResult.get("groups")).get(JahiaOAuthConstants.PROPERTY_VALUE));
                    }

                    systemSession.save();
                }
                return null;
            });
        } catch (RepositoryException e) {
            logger.error(e.getMessage());
        }
    }

    private void removeUserMembership(JCRUserNode userNode) {
        User.getUserMembership(userNode).forEach((key, group) -> {
            try {
                group.removeMember(userNode);
                group.saveSession();
            } catch (RepositoryException e) {
                logger.error("", e);
            }
        });
    }

    private static void updateUserProperty(JCRUserNode userNode, String property, String jcrProperty, Map<String, Object> mapperResult) {
        try {
            if (mapperResult.containsKey(property)) {
                String value = (String) ((Map<String, Object>) mapperResult.get(property)).get(JahiaOAuthConstants.PROPERTY_VALUE);
                userNode.setProperty(jcrProperty, value);
            }
        } catch (RepositoryException e) {
            logger.error("", e);
        }
    }

    private void manageUserGroups(JCRUserNode userNode, JSONArray groups) throws RepositoryException {
        if (groups != null) {
            int nbGroups = groups.length();
            for (int i = 0; i < nbGroups; i++) {
                try {
                    manageUserGroup(userNode, groups.getString(i));
                } catch (JSONException e) {
                    logger.error("Unable to get group in json {}", groups);
                }
            }
        }
    }

    private void manageUserGroup(JCRUserNode userNode, String groupName) throws RepositoryException {
        JCRGroupNode group = jahiaGroupManagerService.lookupGroup(null, groupName);
        if (group == null) {
            group = jahiaGroupManagerService.createGroup(null, groupName, new Properties(), false, userNode.getSession());
        }
        group.addMember(userNode);
        group.saveSession();
    }
}
