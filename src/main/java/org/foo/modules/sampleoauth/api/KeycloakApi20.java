package org.foo.modules.sampleoauth.api;

import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class KeycloakApi20 extends DefaultApi20 {
    private static final ConcurrentMap<String, KeycloakApi20> INSTANCES = new ConcurrentHashMap();
    private final String baseUrlWithRealm;

    protected KeycloakApi20(String baseUrlWithRealm) {
        this.baseUrlWithRealm = baseUrlWithRealm;
    }

    public static KeycloakApi20 instance() {
        return instance("http://localhost:8081/", "master");
    }

    public static KeycloakApi20 instance(String baseUrl, String realm) {
        String defaultBaseUrlWithRealm = composeBaseUrlWithRealm(baseUrl, realm);
        KeycloakApi20 api = INSTANCES.get(defaultBaseUrlWithRealm);
        if (api == null) {
            api = new KeycloakApi20(defaultBaseUrlWithRealm);
            KeycloakApi20 alreadyCreatedApi = INSTANCES.putIfAbsent(defaultBaseUrlWithRealm, api);
            if (alreadyCreatedApi != null) {
                return alreadyCreatedApi;
            }
        }

        return api;
    }

    protected static String composeBaseUrlWithRealm(String baseUrl, String realm) {
        return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + "realms/" + realm;
    }

    public String getAccessTokenEndpoint() {
        return this.baseUrlWithRealm + "/protocol/openid-connect/token";
    }

    protected String getAuthorizationBaseUrl() {
        return this.baseUrlWithRealm + "/protocol/openid-connect/auth";
    }

    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }
}
