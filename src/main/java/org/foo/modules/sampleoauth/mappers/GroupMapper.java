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
import java.util.Arrays;
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
        return Arrays.asList(
                new MappedPropertyInfo(JahiaAuthConstants.SSO_LOGIN, "string", null, true),
                new MappedPropertyInfo("groups", "string", null, true));
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
                    } else {
                        removeUserMembership(userNode);
                    }

                    if (mapperResult.containsKey("groups")) {
                        manageUserGroups(userNode, (String) mapperResult.get("groups").getValue());
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

    private void manageUserGroups(JCRUserNode userNode, String groups) throws RepositoryException {
        if (groups != null) {
            try {
                JSONArray groupsJSON = new JSONArray(groups);
                int nbGroups = groupsJSON.length();
                String groupName;
                JCRGroupNode group;
                for (int i = 0; i < nbGroups; i++) {
                    try {
                        groupName = groupsJSON.getString(i);
                        group = jahiaGroupManagerService.lookupGroup(null, groupName);
                        if (group == null) {
                            group = jahiaGroupManagerService.createGroup(null, groupName, new Properties(), false, userNode.getSession());
                        }
                        group.addMember(userNode);
                        group.saveSession();
                    } catch (JSONException e) {
                        logger.error("Unable to get group in JSON {} at index {}", groups, i);
                    }
                }
            } catch (JSONException e) {
                logger.warn("Unable to parse JSON Groups: {}", groups);
            }
        }
    }
}
