package org.foo.modules.sampleoauth.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.extractors.OAuth2AccessTokenJsonExtractor;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * additionally parses OpenID id_token
 */
public class OpenIdJsonTokenExtractor extends OAuth2AccessTokenJsonExtractor {
    private final static Logger LOGGER = LoggerFactory.getLogger(OpenIdJsonTokenExtractor.class);

    protected OpenIdJsonTokenExtractor() {
    }

    private static class InstanceHolder {

        private static final OpenIdJsonTokenExtractor INSTANCE = new OpenIdJsonTokenExtractor();
    }

    public static OpenIdJsonTokenExtractor instance() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    protected OAuth2AccessToken createToken(String accessToken, String tokenType, Integer expiresIn, String refreshToken, String scope, String response) {
        JsonNode idToken = null;
        try {
            idToken = new ObjectMapper().readTree(response).get("id_token");
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return new OpenIdOAuth2AccessToken(accessToken, tokenType, expiresIn, refreshToken, scope,
                idToken == null ? null : idToken.asText(), response);
    }
}
