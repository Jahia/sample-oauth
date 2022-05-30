package org.foo.modules.sampleoauth.api;

import com.github.scribejava.apis.openid.OpenIdJsonTokenExtractor;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.extractors.TokenExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.Verb;

public class BitbucketApi20 extends DefaultApi20 {
    private static final String HOST = "https://bitbucket.org/site";

    private BitbucketApi20() {
    }

    private static class InstanceHolder {
        private static final BitbucketApi20 INSTANCE = new BitbucketApi20();
    }

    public static BitbucketApi20 instance() {
        return BitbucketApi20.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return HOST + "/oauth2/access_token";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return HOST + "/oauth2/authorize";
    }

    @Override
    public TokenExtractor<OAuth2AccessToken> getAccessTokenExtractor() {
        return OpenIdJsonTokenExtractor.instance();
    }
}
