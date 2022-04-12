package org.foo.modules.sampleoauth.utils;

import com.google.common.net.HttpHeaders;
import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.params.valves.LoginUrlProvider;
import org.jahia.params.valves.LogoutUrlProvider;
import org.jahia.services.sites.JahiaSitesService;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

@Component(service = {LoginUrlProvider.class, LogoutUrlProvider.class}, immediate = true)
public class CustomLoginLogoutUrlProvider implements LoginUrlProvider, LogoutUrlProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomLoginLogoutUrlProvider.class);

    public static final String SESSION_REQUEST_URI = "my.request_uri";

    private SettingsService settingsService;
    private JahiaOAuthService jahiaOAuthService;
    private ConfigurationAdmin configurationAdmin;

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Reference
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    private boolean isNotValid() {
        try {
            Configuration[] configurations = configurationAdmin.listConfigurations("(service.factoryPid=org.jahia.modules.auth)");
            if (configurations != null) {
                return Arrays.stream(configurations)
                        .noneMatch(configuration ->
                                settingsService.getConnectorConfig((String) configuration.getProperties().get(JahiaAuthConstants.SITE_KEY), KeycloakConnectorImpl.KEY) != null);
            }
        } catch (IOException | InvalidSyntaxException e) {
            logger.error("", e);
        }
        return true;
    }

    @Override
    public boolean hasCustomLoginUrl() {
        return true;
    }

    @Override
    public String getLoginUrl(HttpServletRequest httpServletRequest) {
        if (isNotValid()) {
            return null;
        }

        // save the requestUri in the session
        String originalRequestUri = (String) httpServletRequest.getAttribute("javax.servlet.error.request_uri");
        if (originalRequestUri == null) {
            originalRequestUri = httpServletRequest.getRequestURI();
        }
        httpServletRequest.getSession(false).setAttribute(SESSION_REQUEST_URI, originalRequestUri);
        // redirect to SSO
        return jahiaOAuthService.getAuthorizationUrl(
                settingsService.getConnectorConfig(JahiaSitesService.SYSTEM_SITE_KEY, KeycloakConnectorImpl.KEY),
                httpServletRequest.getRequestedSessionId(), null);
    }

    @Override
    public boolean hasCustomLogoutUrl() {
        return true;
    }

    @Override
    public String getLogoutUrl(HttpServletRequest httpServletRequest) {
        if (isNotValid()) {
            return null;
        }

        String authorizationUrl = jahiaOAuthService.getAuthorizationUrl(
                settingsService.getConnectorConfig(JahiaSitesService.SYSTEM_SITE_KEY, KeycloakConnectorImpl.KEY),
                httpServletRequest.getRequestedSessionId(), null);
        if (authorizationUrl == null) {
            return null;
        }

        String host = httpServletRequest.getHeader(HttpHeaders.HOST);
        String scheme = httpServletRequest.getHeader(HttpHeaders.X_FORWARDED_PROTO);
        if (scheme == null) {
            scheme = httpServletRequest.getScheme();
        }
        StringBuilder logoutUrl = new StringBuilder();
        logoutUrl.append(StringUtils.substringBeforeLast(authorizationUrl, "/")).append("/logout?redirect_uri=")
                .append(scheme).append("://").append(host)
                .append(httpServletRequest.getContextPath()).append("/cms/logout");
        return logoutUrl.toString();
    }
}
