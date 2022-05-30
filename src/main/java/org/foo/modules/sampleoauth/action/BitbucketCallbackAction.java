package org.foo.modules.sampleoauth.action;

import org.foo.modules.sampleoauth.api.BitbucketApi20;
import org.foo.modules.sampleoauth.connectors.BitbucketConnectorImpl;
import org.jahia.bin.Action;
import org.jahia.modules.jahiaauth.service.JahiaAuthMapperService;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(service = Action.class, immediate = true)
public class BitbucketCallbackAction extends AbstractCallbackAction {
    private JahiaOAuthService jahiaOAuthService;
    private JahiaAuthMapperService jahiaAuthMapperService;
    private SettingsService settingsService;

    @Reference
    private void refJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    @Reference
    private void refJahiaAuthMapperService(JahiaAuthMapperService jahiaAuthMapperService) {
        this.jahiaAuthMapperService = jahiaAuthMapperService;
    }

    @Reference
    private void refSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @Activate
    private void onActivate() {
        setJahiaOAuthService(jahiaOAuthService);
        setJahiaAuthMapperService(jahiaAuthMapperService);
        setSettingsService(settingsService);
    }

    @Override
    protected String getActionName() {
        return "bitbucketOAuthCallbackAction";
    }

    @Override
    protected String getConnectorService() {
        return BitbucketConnectorImpl.KEY;
    }
}
