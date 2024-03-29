package org.foo.modules.sampleoauth.action;

import org.foo.modules.sampleoauth.connectors.StravaConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.action.ConnectToOAuthProvider;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.http.HttpMethod;

@Component(service = Action.class, immediate = true)
public class StravaConnectAction extends ConnectToOAuthProvider {
    private static final String NAME = "connectToStravaAction";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;

    @Reference(service = JahiaOAuthService.class)
    private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference(service = SettingsService.class)
    private void refSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Activate
    public void onActivate() {
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
        setJahiaOAuthService(jahiaOAuthService);
        setSettingsService(settingsService);
        setName(NAME);
        setConnectorName(StravaConnectorImpl.KEY);
    }
}
