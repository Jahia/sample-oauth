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
<template:addResources type="javascript" resources="i18n/sample-oauth-i18n_${currentResource.locale}.js" var="i18nJSFile"/>
<c:if test="${empty i18nJSFile}">
    <template:addResources type="javascript" resources="i18n/sample-oauth-i18n_en.js"/>
</c:if>
<template:addResources type="javascript" resources="auth/strava-controller.js"/>

<md-card ng-controller="StravaController as strava">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_stravaOAuthView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="strava.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!strava.expandedCard">keyboard_arrow_down</md-icon>
                <md-icon ng-show="strava.expandedCard">keyboard_arrow_up</md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content layout="column" ng-show="strava.expandedCard">
        <form name="stravaForm">
            <md-switch ng-model="strava.enabled">
                <span message-key="label.activate"></span>
            </md-switch>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.apiKey"></label>
                    <input type="text" ng-model="strava.apiKey" name="apiKey" required/>
                    <div ng-messages="stravaForm.apiKey.$error" role="alert">
                        <div ng-message="required" message-key="error.apiKey.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.apiSecret"></label>
                    <input type="text" ng-model="strava.apiSecret" name="apiSecret" required/>
                    <div ng-messages="stravaForm.apiSecret.$error" role="alert">
                        <div ng-message="required" message-key="error.apiSecret.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.scope"></label>
                    <input type="text" ng-model="strava.scope" name="scope"/>
                    <div class="hint" ng-show="!stravaForm.scope.$invalid"
                         message-key="hint.scope"></div>
                    <div ng-messages="stravaForm.scope.$error" role="alert">
                        <div ng-message="required" message-key="error.scope.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="label.callbackURL"></label>
                    <input type="url" ng-model="strava.callbackUrl" name="callbackUrl"/>
                    <div class="hint" ng-show="stravaForm.callbackUrl.$valid"
                         message-key="soauthnt_stravaOAuthView.hint.callbackURL"></div>
                    <div ng-messages="stravaForm.callbackUrl.$error" ng-show="stravaForm.callbackUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                    </div>
                </md-input-container>
            </div>
        </form>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.mappers"
                       ng-click="strava.goToMappers()" ng-show="strava.connectorHasSettings">
            </md-button>
            <md-button class="md-accent" message-key="label.save"
                       ng-click="strava.saveSettings()">
            </md-button>
        </md-card-actions>

    </md-card-content>
</md-card>