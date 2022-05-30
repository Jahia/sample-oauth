package org.foo.modules.sampleoauth.action;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHeaders;
import org.jahia.bin.ActionResult;
import org.jahia.services.content.JCRSessionWrapper;
import org.jahia.services.render.RenderContext;
import org.jahia.services.render.Resource;
import org.jahia.services.render.URLResolver;
import org.springframework.http.HttpMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class AbstractConnectAction extends AbstractOAuthAction {

    public AbstractConnectAction() {
        setName(getActionName());
        setRequireAuthenticatedUser(false);
        setRequiredMethods(HttpMethod.GET.name());
    }

    @Override
    public ActionResult doExecute(HttpServletRequest httpServletRequest, RenderContext renderContext, Resource resource, JCRSessionWrapper session,
                                  Map<String, List<String>> parameters, URLResolver urlResolver) {
        String referer = httpServletRequest.getHeader(HttpHeaders.REFERER);
        if (StringUtils.isNotBlank(referer)) {
            httpServletRequest.getSession(false).setAttribute(SESSION_REQUEST_URI, referer);
        }
        return new ActionResult(HttpServletResponse.SC_OK, getJahiaOAuthService().getAuthorizationUrl(getSettingsService().getConnectorConfig(renderContext.getSite().getSiteKey(), getConnectorService()), httpServletRequest.getRequestedSessionId(), Collections.emptyMap()), true, null);
    }
}
