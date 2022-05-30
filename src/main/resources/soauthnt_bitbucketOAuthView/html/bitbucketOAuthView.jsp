<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="ui" uri="http://www.jahia.org/tags/uiComponentsLib" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="query" uri="http://www.jahia.org/tags/queryLib" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="s" uri="http://www.jahia.org/tags/search" %>
<%@ taglib prefix="json" uri="http://www.atg.com/taglibs/json" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:include view="hidden.generic">
    <template:param name="connectorServiceName" value="BitbucketApi20"/>
    <template:param name="title" value="soauthnt_bitbucketOAuthView"/>
    <template:param name="properties">
        <json:array>
            <json:object>
                <json:property name="name" value="apiKey"/>
                <json:property name="mandatory" value="${true}"/>
            </json:object>
            <json:object>
                <json:property name="name" value="apiSecret"/>
                <json:property name="mandatory" value="${true}"/>
            </json:object>
            <json:object>
                <json:property name="name" value="scope"/>
                <json:property name="mandatory" value="${true}"/>
            </json:object>
            <json:object>
                <json:property name="name" value="callbackUrl"/>
                <json:property name="mandatory" value="${true}"/>
            </json:object>
        </json:array>
    </template:param>
</template:include>
