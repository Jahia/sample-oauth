package org.foo.modules.sampleoauth.connectors;

import com.google.common.net.HttpHeaders;
import org.apache.commons.lang.StringUtils;
import org.foo.modules.sampleoauth.action.AbstractOAuthAction;
import org.jahia.api.content.JCRTemplate;
import org.jahia.exceptions.JahiaException;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.jahia.params.valves.LoginUrlProvider;
import org.jahia.params.valves.LogoutUrlProvider;
import org.jahia.services.content.JCRNodeIteratorWrapper;
import org.jahia.services.sites.JahiaSite;
import org.jahia.services.sites.JahiaSitesService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.query.Query;
import javax.servlet.http.HttpServletRequest;

@Component(service = {LoginUrlProvider.class, LogoutUrlProvider.class}, immediate = true)
public class KeycloakSilentLoginLogoutUrlProvider implements LoginUrlProvider, LogoutUrlProvider {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakSilentLoginLogoutUrlProvider.class);

    private SettingsService settingsService;
    private JahiaOAuthService jahiaOAuthService;
    private JahiaSitesService jahiaSitesService;
    private JCRTemplate jcrTemplate;

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
    private void setJcrTemplate(JCRTemplate jcrTemplate) {
        this.jcrTemplate = jcrTemplate;
    }

    private String getSiteKey(HttpServletRequest httpServletRequest) {
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

    private String getAuthorizationUrl(String siteKey, String sessionId) {
        ConnectorConfig connectorConfig = settingsService.getConnectorConfig(siteKey, KeycloakConnectorImpl.KEY);
        if (connectorConfig == null) {
            return null;
        }
        return jahiaOAuthService.getAuthorizationUrl(connectorConfig, sessionId, null);
    }

    @Override
    public boolean hasCustomLoginUrl() {
        return true;
    }

    @Override
    public String getLoginUrl(HttpServletRequest httpServletRequest) {
        String siteKey = getSiteKey(httpServletRequest);
        if (siteKey != null && isLoginModalEnabled(siteKey)) {
            httpServletRequest.setAttribute("isLoginModalEnabled", true);
            return null;
        }

        String authorizationUrl = getAuthorizationUrl(siteKey, httpServletRequest.getRequestedSessionId());
        if (authorizationUrl == null) {
            return null;
        }

        // save the requestUri in the session
        String originalRequestUri = (String) httpServletRequest.getAttribute("javax.servlet.error.request_uri");
        if (originalRequestUri == null) {
            originalRequestUri = httpServletRequest.getRequestURI();
        }
        httpServletRequest.getSession(false).setAttribute(AbstractOAuthAction.SESSION_REQUEST_URI, originalRequestUri);
        // redirect to SSO
        return authorizationUrl;
    }

    @Override
    public boolean hasCustomLogoutUrl() {
        return true;
    }

    @Override
    public String getLogoutUrl(HttpServletRequest httpServletRequest) {
        String siteKey = getSiteKey(httpServletRequest);
        if (siteKey != null && isLoginModalEnabled(siteKey)) {
            return null;
        }

        String authorizationUrl = getAuthorizationUrl(siteKey, httpServletRequest.getRequestedSessionId());
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

    private boolean isLoginModalEnabled(String siteKey) {
        try {
            JCRNodeIteratorWrapper it = jcrTemplate.doExecuteWithSystemSession(
                    systemSession -> systemSession.getWorkspace().getQueryManager().createQuery(
                                    "SELECT * FROM [soauthnt:loginModal] WHERE ISDESCENDANTNODE('/sites/" + siteKey + "/home')", Query.JCR_SQL2)
                            .execute().getNodes());
            return it.hasNext() && it.nextNode() != null;
        } catch (RepositoryException e) {
            logger.error("", e);
            return false;
        }
    }
}
