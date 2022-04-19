/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2022 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms &amp; Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.foo.modules.sampleoauth.builder;

import com.github.scribejava.apis.MicrosoftAzureActiveDirectory20Api;
import com.github.scribejava.core.builder.api.DefaultApi20;
import org.jahia.modules.jahiaauth.service.ConnectorConfig;
import org.jahia.modules.jahiaoauth.service.JahiaOAuthAPIBuilder;

/**
 * Wrapper of the Microsoft Azure connector
 */
public class AzureConnectorBuilder implements JahiaOAuthAPIBuilder {
    private static final String TENANT_ID = "tenantID";

    @Override
    public DefaultApi20 build(ConnectorConfig connectorConfig) {
        return MicrosoftAzureActiveDirectory20Api.custom(connectorConfig.getProperty(TENANT_ID));
    }
}
