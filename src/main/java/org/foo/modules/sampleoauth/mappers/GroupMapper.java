package org.foo.modules.sampleoauth.mappers;

import org.jahia.api.usermanager.JahiaUserManagerService;
import org.jahia.modules.jahiaauth.service.*;
import org.jahia.services.content.JCRTemplate;
import org.jahia.services.content.decorator.JCRGroupNode;
import org.jahia.services.content.decorator.JCRUserNode;
import org.jahia.services.usermanager.JahiaGroupManagerService;
import org.jahia.taglibs.user.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Component(service = Mapper.class, property = {JahiaAuthConstants.MAPPER_SERVICE_NAME + "=" + GroupMapper.MAPPER_NAME}, immediate = true)
public class GroupMapper implements Mapper {
    private static final Logger logger = LoggerFactory.getLogger(GroupMapper.class);

    public static final String MAPPER_NAME = "groupMapper";

    private JCRTemplate jcrTemplate;
    private JahiaUserManagerService jahiaUserManagerService;
    private JahiaGroupManagerService jahiaGroupManagerService;

    @Reference
    private void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    @Reference
    private void setJahiaUserManagerService(JahiaUserManagerService jahiaUserManagerService) {
        this.jahiaUserManagerService = jahiaUserManagerService;
    }

    @Reference
    private void setJahiaGroupManagerService(JahiaGroupManagerService jahiaGroupManagerService) {
        this.jahiaGroupManagerService = jahiaGroupManagerService;
    }

    @Override
    public List<MappedPropertyInfo> getProperties() {
        return Collections.singletonList(new MappedPropertyInfo("group", "string", null, true));
    }

    @Override
    public void executeMapper(Map<String, MappedProperty> mapperResult, MapperConfig mapperConfig) {
        try {
            jcrTemplate.doExecuteWithSystemSession(systemSession -> {
                if (mapperResult.containsKey(JahiaAuthConstants.SSO_LOGIN)) {
                    String userId = (String) mapperResult.get(JahiaAuthConstants.SSO_LOGIN).getValue();
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
                        manageUserGroups(userNode, (JSONArray) mapperResult.get("groups").getValue());
                    }

                    systemSession.save();
                }
                return null;
            });
        } catch (RepositoryException e) {
            logger.error("", e);
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

    private static void updateUserProperty(JCRUserNode userNode, String property, String jcrProperty, Map<String, MappedProperty> mapperResult) {
        try {
            if (mapperResult.containsKey(property)) {
                String value = (String) mapperResult.get(property).getValue();
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
