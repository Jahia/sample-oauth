package org.foo.modules.sampleoauth.action;

import org.foo.modules.sampleoauth.connectors.BitbucketConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Action.class, immediate = true)
public class BitbucketConnectAction extends AbstractConnectAction {
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
    private void onActivate() {
        setJahiaOAuthService(jahiaOAuthService);
        setSettingsService(settingsService);
    }

    @Override
    protected String getActionName() {
        return "connectToBitbucketApi20Action";
    }

    @Override
    protected String getConnectorService() {
        return BitbucketConnectorImpl.KEY;
    }
}
