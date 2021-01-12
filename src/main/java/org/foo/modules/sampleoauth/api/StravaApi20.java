package org.foo.modules.sampleoauth.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

public final class StravaApi20 extends DefaultApi20 {
    private StravaApi20() {
    }

    private static class InstanceHolder {
        private static final StravaApi20 INSTANCE = new StravaApi20();
    }

    public static StravaApi20 instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://www.strava.com/oauth/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://www.strava.com/oauth/authorize";
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }
}
