package org.foo.modules.sampleoauth.filters;

import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.jahia.api.Constants;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = RenderFilter.class, immediate = true)
public class KeycloakSilentLoginFilter extends AbstractFilter {
    private SettingsService settingsService;

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public KeycloakSilentLoginFilter() {
        setApplyOnNodeTypes("soauthnt:keycloakSilentLogin");
        setApplyOnModes(Constants.LIVE_WORKSPACE);
        setPriority(17.0f);
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) {
        ConnectorConfig connectorConfig = settingsService.getConnectorConfig(renderContext.getSite().getSiteKey(), KeycloakConnectorImpl.KEY);
        renderContext.getRequest().setAttribute("auth.url", connectorConfig.getProperty(KeycloakConnectorImpl.BASEURL));
        renderContext.getRequest().setAttribute("auth.realm", connectorConfig.getProperty(KeycloakConnectorImpl.REALM));
        renderContext.getRequest().setAttribute("auth.clientId", connectorConfig.getProperty(JahiaOAuthConstants.PROPERTY_API_KEY));
        return null;
    }
}
