package org.foo.modules.sampleoauth.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.Verb;

public class GitlabApi20 extends DefaultApi20 {
    public static final String HOST = "https://gitlab.com";

    private GitlabApi20() {
    }

    private static class InstanceHolder {
        private static final GitlabApi20 INSTANCE = new GitlabApi20();
    }

    public static GitlabApi20 instance() {
        return GitlabApi20.InstanceHolder.INSTANCE;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return HOST + "/oauth/token";
    }

    @Override
    public Verb getAccessTokenVerb() {
        return Verb.POST;
    }

    @Override
    protected String getAuthorizationBaseUrl() {
        return HOST + "/oauth/authorize";
    }
}
