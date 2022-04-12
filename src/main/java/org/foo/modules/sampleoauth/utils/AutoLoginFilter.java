package org.foo.modules.sampleoauth.utils;

import org.foo.modules.sampleoauth.connectors.KeycloakConnectorImpl;
import org.jahia.api.Constants;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.jahia.services.sites.JahiaSitesService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = RenderFilter.class, immediate = true)
public class AutoLoginFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(AutoLoginFilter.class);

    private SettingsService settingsService;

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public AutoLoginFilter() {
        setApplyOnNodeTypes("soauthnt:autoLogin");
        setApplyOnModes(Constants.LIVE_WORKSPACE);
        setPriority(17.0f);
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        ConnectorConfig connectorConfig = settingsService.getConnectorConfig(JahiaSitesService.SYSTEM_SITE_KEY, KeycloakConnectorImpl.KEY);
        renderContext.getRequest().setAttribute("auth.url", connectorConfig.getProperty(KeycloakConnectorImpl.BASEURL));
        renderContext.getRequest().setAttribute("auth.realm", connectorConfig.getProperty(KeycloakConnectorImpl.REALM));
        return null;
    }
}
