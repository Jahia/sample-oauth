package org.foo.modules.sampleoauth.connectors;

import org.jahia.modules.jahiaauth.service.*;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.Map;

@Component(service = ConnectorResultProcessor.class, immediate = true)
public class TokenDataResultProcessor implements ConnectorResultProcessor {
    public static final String MAPPER_NAME = "tokenDataInfo";

    private JahiaAuthMapperService jahiaAuthMapperService;

    @Reference
    private void setJahiaAuthMapperService(JahiaAuthMapperService jahiaAuthMapperService) {
        this.jahiaAuthMapperService = jahiaAuthMapperService;
    }

    @Override
    public void execute(ConnectorConfig connectorConfig, Map<String, Object> results) {
        // store tokenData to cache
        jahiaAuthMapperService.cacheMapperResults(MAPPER_NAME, RequestContextHolder.getRequestAttributes().getSessionId(),
                Collections.singletonMap(JahiaOAuthConstants.TOKEN_DATA, new MappedProperty(
                        new MappedPropertyInfo(JahiaOAuthConstants.TOKEN_DATA), results.get(JahiaOAuthConstants.TOKEN_DATA))));
    }
}
