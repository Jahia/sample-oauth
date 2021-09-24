<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
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
<template:addResources type="javascript" resources="auth/okta-connector-controller.js"/>

<md-card ng-controller="OktaController as okta">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_oktaOAuthView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="okta.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!okta.expandedCard">keyboard_arrow_down</md-icon>
                <md-icon ng-show="okta.expandedCard">keyboard_arrow_up</md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content layout="column" ng-show="okta.expandedCard">
        <form name="oktaForm">
            <md-switch ng-model="okta.isActivate">
                <span message-key="label.activate"></span>
            </md-switch>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.apiKey"></label>
                    <input type="text" ng-model="okta.apiKey" name="apiKey" required/>
                    <div ng-messages="oktaForm.apiKey.$error" role="alert">
                        <div ng-message="required" message-key="error.apiKey.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.apiSecret"></label>
                    <input type="text" ng-model="okta.apiSecret" name="apiSecret" required/>
                    <div ng-messages="oktaForm.apiSecret.$error" role="alert">
                        <div ng-message="required" message-key="error.apiSecret.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.scope"></label>
                    <input type="text" ng-model="okta.scope" name="scope"/>
                    <div class="hint" ng-show="!oktaForm.scope.$invalid"
                         message-key="hint.scope"></div>
                    <div ng-messages="oktaForm.scope.$error" role="alert">
                        <div ng-message="required" message-key="error.scope.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.organization"></label>
                    <input type="text" ng-model="okta.organization" name="organization"/>
                    <div class="hint" ng-show="!oktaForm.organization.$invalid"
                         message-key="hint.organization"></div>
                    <div ng-messages="oktaForm.organization.$error" role="alert">
                        <div ng-message="required" message-key="error.organization.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="label.callbackURL"></label>
                    <input type="url" ng-model="okta.callbackUrl" name="callbackUrl"/>
                    <md-icon class="md-icon-button" ng-click="okta.addUrl(oktaForm.callbackUrl.$valid)">add
                    </md-icon>
                    <div class="hint" ng-show="oktaForm.callbackUrl.$valid"
                         message-key="soauthnt_oktaOAuthView.hint.callbackURL"></div>
                    <div ng-messages="oktaForm.callbackUrl.$error" ng-show="oktaForm.callbackUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                    </div>
                </md-input-container>
            </div>
            <div layout="row" ng-show="okta.callbackUrls.length > 0">
                <md-list flex>
                    <md-list-item ng-repeat="callbackUrl in okta.callbackUrls track by $index">
                        <p>{{ callbackUrl }}</p>
                        <md-button class="md-warn" ng-click="okta.removeUrl($index)">remove</md-button>
                    </md-list-item>
                </md-list>
            </div>
        </form>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.mappers"
                       ng-click="okta.goToMappers()" ng-show="okta.connectorHasSettings">
            </md-button>
            <md-button class="md-accent" message-key="label.save"
                       ng-click="okta.saveSettings()">
            </md-button>
        </md-card-actions>

    </md-card-content>
</md-card>
