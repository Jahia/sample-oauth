package org.foo.modules.sampleoauth.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.oauth2.clientauthentication.ClientAuthentication;
import com.github.scribejava.core.oauth2.clientauthentication.RequestBodyAuthenticationScheme;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class AzureB2CApi extends DefaultApi20 {
    private static final String MSFT_LOGIN_URL = "https://login.microsoftonline.com/";
    private static final String COMMON_TENANT = "common";
    private static final String OAUTH_2 = "/oauth2";
    private static final String ENDPOINT_VERSION_PATH = "/v2.0";

    private static final ConcurrentMap<String, AzureB2CApi> INSTANCES = new ConcurrentHashMap<>();

    private final String host;
    private final String tenant;
    private final String policyName;
    private final String hostWithTenant;

    private AzureB2CApi(String host, String tenant, String policyName) {
        this.host = host;
        this.tenant = tenant;
        this.policyName = policyName;
        this.hostWithTenant = host + "/" + tenant;
    }

    public static AzureB2CApi instance() {
        return instance(MSFT_LOGIN_URL, COMMON_TENANT, "");
    }

    public static AzureB2CApi instance(String host, String tenant, String policyName) {
        return INSTANCES.computeIfAbsent(host + "/" + tenant + "/" + policyName, hostWithTenant -> new AzureB2CApi(host, tenant, policyName));
    }

    @Override
    public String getAccessTokenEndpoint() {
        return host + "/" + policyName + OAUTH_2 + ENDPOINT_VERSION_PATH + "/token";
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return host + "/" + policyName + OAUTH_2 + ENDPOINT_VERSION_PATH + "/authorize";
    }

    @Override
    public ClientAuthentication getClientAuthentication() {
        return RequestBodyAuthenticationScheme.instance();
    }
}
