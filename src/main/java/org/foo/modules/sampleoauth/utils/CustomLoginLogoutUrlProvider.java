package org.foo.modules.sampleoauth.utils;

import com.google.common.net.HttpHeaders;
import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.jahia.exceptions.JahiaException;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.params.valves.LoginUrlProvider;
import org.jahia.params.valves.LogoutUrlProvider;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

@Component(service = {LoginUrlProvider.class, LogoutUrlProvider.class}, immediate = true)
public class CustomLoginLogoutUrlProvider implements LoginUrlProvider, LogoutUrlProvider {
    private static final Logger logger = LoggerFactory.getLogger(CustomLoginLogoutUrlProvider.class);

    public static final String SESSION_REQUEST_URI = "my.request_uri";

    private SettingsService settingsService;
    private JahiaOAuthService jahiaOAuthService;
    private JahiaSitesService jahiaSitesService;
    private KeycloakConnectorImpl keycloakConnector;

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Reference
    private void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void setJahiaSitesService(JahiaSitesService jahiaSitesService) {
        this.jahiaSitesService = jahiaSitesService;
    }

    @Reference
    private void setKeycloakConnector(KeycloakConnectorImpl keycloakConnector) {
        this.keycloakConnector = keycloakConnector;
    }

    private static String getSiteKey(JahiaSitesService jahiaSitesService, HttpServletRequest httpServletRequest) {
        String siteKey;
        try {
            JahiaSite jahiaSite = jahiaSitesService.getSiteByServerName(httpServletRequest.getServerName());
            if (jahiaSite != null) {
                siteKey = jahiaSite.getSiteKey();
            } else {
                siteKey = jahiaSitesService.getDefaultSite().getSiteKey();
            }
        } catch (JahiaException e) {
            siteKey = jahiaSitesService.getDefaultSite().getSiteKey();
        }
        return siteKey;
    }

    private String getAuthorizationUrl(HttpServletRequest httpServletRequest) {
        ConnectorConfig connectorConfig = settingsService.getConnectorConfig(getSiteKey(jahiaSitesService, httpServletRequest), KeycloakConnectorImpl.KEY);
        if (connectorConfig == null) {
            return null;
        }
        // Fix hack to refresh OAuth configuration
        // TODO: Use https://github.com/Jahia/jahia-oauth/blob/master/src/main/java/org/jahia/modules/jahiaoauth/service/JahiaOAuthAPIBuilder.java
        keycloakConnector.validateSettings(connectorConfig);
        return jahiaOAuthService.getAuthorizationUrl(connectorConfig, httpServletRequest.getRequestedSessionId(), null);
    }

    @Override
    public boolean hasCustomLoginUrl() {
        return true;
    }

    @Override
    public String getLoginUrl(HttpServletRequest httpServletRequest) {
        String authorizationUrl = getAuthorizationUrl(httpServletRequest);
        if (authorizationUrl == null) {
            return null;
        }

        // save the requestUri in the session
        String originalRequestUri = (String) httpServletRequest.getAttribute("javax.servlet.error.request_uri");
        if (originalRequestUri == null) {
            originalRequestUri = httpServletRequest.getRequestURI();
        }
        httpServletRequest.getSession(false).setAttribute(SESSION_REQUEST_URI, originalRequestUri);
        // redirect to SSO
        return authorizationUrl;
    }

    @Override
    public boolean hasCustomLogoutUrl() {
        return true;
    }

    @Override
    public String getLogoutUrl(HttpServletRequest httpServletRequest) {
        String authorizationUrl = getAuthorizationUrl(httpServletRequest);
        if (authorizationUrl == null) {
            return null;
        }

        String scheme = httpServletRequest.getHeader(HttpHeaders.X_FORWARDED_PROTO);
        if (scheme == null) {
            scheme = httpServletRequest.getScheme();
        }
        StringBuilder logoutUrl = new StringBuilder();
        logoutUrl.append(StringUtils.substringBeforeLast(authorizationUrl, "/")).append("/logout?redirect_uri=")
                .append(scheme).append("://").append(httpServletRequest.getHeader(HttpHeaders.HOST))
                .append(httpServletRequest.getContextPath()).append("/cms/logout");
        return logoutUrl.toString();
    }
}
