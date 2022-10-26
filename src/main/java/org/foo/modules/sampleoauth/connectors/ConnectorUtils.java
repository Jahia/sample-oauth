package org.foo.modules.sampleoauth.connectors;

import org.apache.commons.lang.StringUtils;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.osgi.BundleUtils;
import org.jahia.services.render.RenderContext;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;

public final class ConnectorUtils {
    private ConnectorUtils() {
    }

    private static String getConnectorProperty(final String siteKey, final String connectorName, final String property, Supplier<String> fallback) {
        SettingsService settingsService = BundleUtils.getOsgiService(SettingsService.class, null);
        if (settingsService == null) {
            return fallback.get();
        }
        ConnectorConfig connectorConfig = settingsService.getConnectorConfig(siteKey, connectorName);
        if (connectorConfig == null) {
            return fallback.get();
        }
        return connectorConfig.getProperty(property);
    }

    public static String getConnectorProperty(final RenderContext renderContext, final String connectorName, final String property) {
        return getConnectorProperty(renderContext.getSite().getSiteKey(), connectorName, property, () -> null);
    }

    public static String getServerLocation(final RenderContext renderContext) {
        final HttpServletRequest request = renderContext.getRequest();
        return getConnectorProperty(renderContext.getSite().getSiteKey(), "Saml", "serverLocation", () -> {
            String serverName = request.getHeader("X-Forwarded-Server");
            if (StringUtils.isEmpty(serverName)) {
                serverName = request.getServerName();
            }

            try {
                URL url = new URL(request.getScheme(), serverName, request.getServerPort(), request.getContextPath());
                return url.toString();
            } catch (MalformedURLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
