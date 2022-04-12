package org.foo.modules.sampleoauth.mappers;

import org.jahia.modules.jahiaauth.service.*;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthConstants;
import org.osgi.service.component.annotations.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component(service = Mapper.class, property = {JahiaAuthConstants.MAPPER_SERVICE_NAME + "=" + TokenDataMapper.MAPPER_NAME}, immediate = true)
public class TokenDataMapper implements Mapper {
    public static final String MAPPER_NAME = "tokenDataMapper";

    @Override
    public List<MappedPropertyInfo> getProperties() {
        return Collections.singletonList(new MappedPropertyInfo(JahiaOAuthConstants.TOKEN_DATA, "string", null, true));
    }

    @Override
    public void executeMapper(Map<String, MappedProperty> mapperResult, MapperConfig mapperConfig) {
        RequestContextHolder.getRequestAttributes().setAttribute(JahiaOAuthConstants.TOKEN_DATA,
                mapperResult.get(JahiaOAuthConstants.TOKEN_DATA).getValue(), RequestAttributes.SCOPE_GLOBAL_SESSION);
    }
}
