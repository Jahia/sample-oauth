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
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="javascript" resources="i18n/sample-oauth-i18n_${currentResource.locale}.js"
                       var="i18nJSFile"/>
<c:if test="${empty i18nJSFile}">
    <template:addResources type="javascript" resources="i18n/sample-oauth-i18n.js"/>
</c:if>
<template:addResources type="javascript" resources="auth/okta-mapper-controller.js"/>

<md-card ng-controller="OktaMapperController as okta" class="ng-cloak">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_oktaOAuthMapperView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="okta.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!okta.expandedCard">
                    keyboard_arrow_down
                </md-icon>
                <md-icon ng-show="okta.expandedCard">
                    keyboard_arrow_up
                </md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content ng-show="okta.expandedCard">

        <div class="md-subhead joa-description">
            <span message-key="soauthnt_oktaOAuthMapperView.message.description"></span>
        </div>

        <div flex="35" layout="row" layout-align="start center">
            <md-switch ng-model="okta.isActivate">
                <span message-key="label.activate"></span>
            </md-switch>

            <div flex="10"></div>

            <md-input-container flex>
                <label message-key="label.fieldFromConnector"></label>
                <md-select ng-model="okta.selectedPropertyFromConnector" ng-change="okta.addMapping()">
                    <md-optgroup>
                        <md-option ng-repeat="connectorProperty in okta.connectorProperties | selectable:{mapping:okta.mapping,key:'connector'} | orderBy:okta.orderByConnector" ng-value="connectorProperty">
                            {{ okta.getConnectorI18n(connectorProperty.name) }}
                        </md-option>
                    </md-optgroup>
                </md-select>
            </md-input-container>
        </div>

        <section ng-show="okta.mapping.length > 0">
            <hr />
            <div layout="row" ng-repeat="mapped in okta.mapping track by $index" layout-align="start center">
                <div flex="45">
                    {{ okta.getConnectorI18n(mapped.connector.name) }}
                </div>
                <div flex="45" layout="row">
                    <md-input-container flex>
                        <label message-key="label.fieldFromMapper"></label>
                        <md-select ng-model="mapped.mapper" ng-model-options="{trackBy: '$value.name'}">
                            <md-optgroup>
                                <md-option ng-repeat="mapperProperty in okta.mapperProperties | selectable:{mapping:okta.mapping,key:'mapper',selected:mapped.mapper} | typeMatch:mapped.connector.valueType | orderBy:okta.orderByMapper" ng-value="mapperProperty">
                                    {{ okta.getMapperI18n(mapperProperty.name) }} <span ng-if="mapperProperty.mandatory" class="joa-mandatory-property" message-key="label.mandatory"></span>
                                </md-option>
                            </md-optgroup>
                        </md-select>
                    </md-input-container>
                </div>
                <div flex="10" layout="row" layout-align="end center">
                    <md-button class="md-icon-button"
                               ng-class="{ 'md-warn': hover }"
                               ng-mouseenter="hover = true"
                               ng-mouseleave="hover = false"
                               ng-click="okta.removeMapping($index)">
                        <md-tooltip md-direction="left">
                            <span message-key="tooltip.removeMappedField"></span>
                        </md-tooltip>
                        <md-icon>remove_circle_outline</md-icon>
                    </md-button>
                </div>
            </div>
        </section>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.save"
                       ng-click="okta.saveMapperSettings()">
            </md-button>
        </md-card-actions>
    </md-card-content>
</md-card>
