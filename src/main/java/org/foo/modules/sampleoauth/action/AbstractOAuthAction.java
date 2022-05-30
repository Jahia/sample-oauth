package org.foo.modules.sampleoauth.action;

import org.jahia.bin.Action;
import org.jahia.modules.jahiaauth.service.SettingsService;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthService;

public abstract class AbstractOAuthAction extends Action {

    public static final String SESSION_REQUEST_URI = "my.request_uri";

    private JahiaOAuthService jahiaOAuthService;
    private SettingsService settingsService;

    protected JahiaOAuthService getJahiaOAuthService() {
        return jahiaOAuthService;
    }

    protected void setJahiaOAuthService(JahiaOAuthService jahiaOAuthService) {
        this.jahiaOAuthService = jahiaOAuthService;
    }

    protected SettingsService getSettingsService() {
        return settingsService;
    }

    protected void setSettingsService(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    protected abstract String getActionName();
    protected abstract String getConnectorService();
}
