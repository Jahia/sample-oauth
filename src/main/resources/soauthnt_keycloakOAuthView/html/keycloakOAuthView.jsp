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
<template:addResources type="javascript" resources="i18n/sample-oauth-i18n_${renderContext.UILocale}.js"
                       var="i18nJSFile"/>
<c:if test="${empty i18nJSFile}">
    <template:addResources type="javascript" resources="i18n/sample-oauth-i18n_en.js"/>
</c:if>
<template:addResources type="javascript" resources="auth/keycloak-controller.js"/>

<md-card ng-controller="KeycloakController as keycloak">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_keycloakOAuthView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="keycloak.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!keycloak.expandedCard">keyboard_arrow_down</md-icon>
                <md-icon ng-show="keycloak.expandedCard">keyboard_arrow_up</md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content layout="column" ng-show="keycloak.expandedCard">
        <form name="keycloakForm">

            <md-switch ng-model="keycloak.enabled">
                <span message-key="label.activate"></span>
            </md-switch>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="soauthnt_keycloakOAuthView.label.realm"></label>
                    <input type="text" ng-model="keycloak.realm" name="realm" required/>
                    <div ng-messages="keycloakForm.realm.$error" role="alert">
                        <div ng-message="required" message-key="soauthnt_keycloakOAuthView.error.realm.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.apiKey"></label>
                    <input type="text" ng-model="keycloak.apiKey" name="apiKey" required/>
                    <div ng-messages="keycloakForm.apiKey.$error" role="alert">
                        <div ng-message="required" message-key="error.apiKey.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.scope"></label>
                    <input type="text" ng-model="keycloak.scope" name="scope" required/>
                    <div class="hint" ng-show="!keycloakForm.scope.$invalid"
                         message-key="hint.scope"></div>
                    <div ng-messages="keycloakForm.scope.$error" role="alert">
                        <div ng-message="required" message-key="error.scope.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="soauthnt_keycloakOAuthView.label.baseUrl"></label>
                    <input type="url" ng-model="keycloak.baseUrl" name="baseUrl" required/>
                    <div ng-messages="keycloakForm.baseUrl.$error" ng-show="keycloakForm.baseUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                        <div ng-message="required" message-key="soauthnt_keycloakOAuthView.error.baseUrl.required"></div>
                    </div>
                </md-input-container>
            </div>

            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="label.callbackURL"></label>
                    <input type="url" ng-model="keycloak.callbackUrl" name="callbackUrl" required/>
                    <div class="hint" ng-show="keycloakForm.callbackUrl.$valid"
                         message-key="soauthnt_keycloakOAuthView.hint.callbackURL"></div>
                    <div ng-messages="keycloakForm.callbackUrl.$error" ng-show="keycloakForm.callbackUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                        <div ng-message="required" message-key="soauthnt_keycloakOAuthView.error.callbackUrl.required"></div>
                    </div>
                </md-input-container>
            </div>
        </form>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.mappers"
                       ng-click="keycloak.goToMappers()" ng-show="keycloak.connectorHasSettings">
            </md-button>
            <md-button class="md-accent" message-key="label.save"
                       ng-click="keycloak.saveSettings()">
            </md-button>
        </md-card-actions>

    </md-card-content>
</md-card>
