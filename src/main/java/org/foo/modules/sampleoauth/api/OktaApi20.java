package org.foo.modules.sampleoauth.api;

import com.github.scribejava.core.builder.api.DefaultApi20;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class OktaApi20 extends DefaultApi20 {
    private static final ConcurrentMap<String, OktaApi20> INSTANCES = new ConcurrentHashMap<>();
    private final String organization;

    private OktaApi20(String organization) {
        this.organization = organization;
    }

    public static OktaApi20 instance(String organization) {
        return INSTANCES.computeIfAbsent(organization, org -> new OktaApi20(org));
    }

    @Override
    public String getAccessTokenEndpoint() {
        return String.format("https://%s/oauth2/v1/token", organization);
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return String.format("https://%s/oauth2/v1/authorize", organization);
    }
}
