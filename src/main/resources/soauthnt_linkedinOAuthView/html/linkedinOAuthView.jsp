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
<template:addResources type="javascript" resources="auth/linkedin-connector-controller.js"/>

<md-card ng-controller="LinkedinController as linkedin">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_linkedinOAuthView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="linkedin.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!linkedin.expandedCard">keyboard_arrow_down</md-icon>
                <md-icon ng-show="linkedin.expandedCard">keyboard_arrow_up</md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content layout="column" ng-show="linkedin.expandedCard">
        <form name="linkedinForm">
            <md-switch ng-model="linkedin.isActivate">
                <span message-key="label.activate"></span>
            </md-switch>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.apiKey"></label>
                    <input type="text" ng-model="linkedin.apiKey" name="apiKey" required/>
                    <div ng-messages="linkedinForm.apiKey.$error" role="alert">
                        <div ng-message="required" message-key="error.apiKey.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.apiSecret"></label>
                    <input type="text" ng-model="linkedin.apiSecret" name="apiSecret" required/>
                    <div ng-messages="linkedinForm.apiSecret.$error" role="alert">
                        <div ng-message="required" message-key="error.apiSecret.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.scope"></label>
                    <input type="text" ng-model="linkedin.scope" name="scope"/>
                    <div class="hint" ng-show="!linkedinForm.scope.$invalid"
                         message-key="hint.scope"></div>
                    <div ng-messages="linkedinForm.scope.$error" role="alert">
                        <div ng-message="required" message-key="error.scope.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="label.callbackURL"></label>
                    <input type="url" ng-model="linkedin.callbackUrl" name="callbackUrl"/>
                    <md-icon class="md-icon-button" ng-click="linkedin.addUrl(linkedinForm.callbackUrl.$valid)">add
                    </md-icon>
                    <div class="hint" ng-show="linkedinForm.callbackUrl.$valid"
                         message-key="soauthnt_linkedinOAuthView.hint.callbackURL"></div>
                    <div ng-messages="linkedinForm.callbackUrl.$error" ng-show="linkedinForm.callbackUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                    </div>
                </md-input-container>
            </div>
            <div layout="row" ng-show="linkedin.callbackUrls.length > 0">
                <md-list flex>
                    <md-list-item ng-repeat="callbackUrl in linkedin.callbackUrls track by $index">
                        <p>{{ callbackUrl }}</p>
                        <md-button class="md-warn" ng-click="linkedin.removeUrl($index)">remove</md-button>
                    </md-list-item>
                </md-list>
            </div>
        </form>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.mappers"
                       ng-click="linkedin.goToMappers()" ng-show="linkedin.connectorHasSettings">
            </md-button>
            <md-button class="md-accent" message-key="label.save"
                       ng-click="linkedin.saveSettings()">
            </md-button>
        </md-card-actions>

    </md-card-content>
</md-card>
