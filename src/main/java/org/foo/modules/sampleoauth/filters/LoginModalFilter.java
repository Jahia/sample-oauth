package org.foo.modules.sampleoauth.filters;

import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.ConnectorService;
import org.jahia.modules.jahiaauth.service.JahiaAuthConstants;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.filter.AbstractFilter;
import org.jahia.services.render.filter.RenderChain;
import org.jahia.services.render.filter.RenderFilter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Component(service = RenderFilter.class, immediate = true)
public class LoginModalFilter extends AbstractFilter {
    private static final Logger logger = LoggerFactory.getLogger(LoginModalFilter.class);

    private SettingsService settingsService;

    @Reference
    private void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    public LoginModalFilter() {
        setApplyOnNodeTypes("soauthnt:loginModal");
    }

    @Override
    public String prepare(RenderContext renderContext, Resource resource, RenderChain chain) throws Exception {
        final String siteKey = renderContext.getSite().getSiteKey();
        List<String> technicalConnectors = new ArrayList<>();
        List<String> nonTechnicalConnectors = new ArrayList<>();
        settingsService.getSettings(siteKey).getValues(null).getSubValueKeys().forEach(connector -> {
            ConnectorConfig connectorConfig = settingsService.getConnectorConfig(siteKey, connector);
            ConnectorService connectorService = BundleUtils.getOsgiService(ConnectorService.class, "(" + JahiaAuthConstants.CONNECTOR_SERVICE_NAME + "=" + connector + ")");
            if (Boolean.parseBoolean(connectorConfig.getProperty("enabled"))) {
                if (Boolean.parseBoolean(connectorConfig.getProperty("isTechnical"))) {
                    technicalConnectors.add(connector);
                } else {
                    nonTechnicalConnectors.add(connector);
                }
            }
        });

        renderContext.getRequest().setAttribute("technicalConnectors", technicalConnectors);
        renderContext.getRequest().setAttribute("nonTechnicalConnectors", nonTechnicalConnectors);
        return null;
    }
}
