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
    <template:addResources type="javascript" resources="i18n/sample-oauth-i18n_en.js"/>
</c:if>
<template:addResources type="javascript" resources="auth/salesforce-controller.js"/>

<md-card ng-controller="SalesforceController as salesforce">
    <div layout="row">
        <md-card-title flex>
            <md-card-title-text>
                <span class="md-headline" message-key="soauthnt_salesforceOAuthView"></span>
            </md-card-title-text>
        </md-card-title>
        <div flex layout="row" layout-align="end center">
            <md-button class="md-icon-button" ng-click="salesforce.toggleCard()">
                <md-tooltip md-direction="top">
                    <span message-key="tooltip.toggleSettings"></span>
                </md-tooltip>
                <md-icon ng-show="!salesforce.expandedCard">keyboard_arrow_down</md-icon>
                <md-icon ng-show="salesforce.expandedCard">keyboard_arrow_up</md-icon>
            </md-button>
        </div>
    </div>

    <md-card-content layout="column" ng-show="salesforce.expandedCard">
        <form name="salesforceForm">

            <md-switch ng-model="salesforce.enabled">
                <span message-key="label.activate"></span>
            </md-switch>

            <div layout="row">
                <md-input-container flex>
                    <label message-key="label.apiKey"></label>
                    <input type="text" ng-model="salesforce.apiKey" name="apiKey" required/>
                    <div ng-messages="salesforceForm.apiKey.$error" role="alert">
                        <div ng-message="required" message-key="error.apiKey.required"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="label.apiSecret"></label>
                    <input type="text" ng-model="salesforce.apiSecret" name="apiSecret" required/>
                    <div ng-messages="salesforceForm.apiSecret.$error" role="alert">
                        <div ng-message="required" message-key="error.apiSecret.required"></div>
                    </div>
                </md-input-container>
            </div>


            <div layout="row">
                <md-input-container class="md-block" flex>
                    <label message-key="label.callbackURL"></label>
                    <input type="url" ng-model="salesforce.callbackUrl" name="callbackUrl"/>
                    <div class="hint" ng-show="salesforceForm.callbackUrl.$valid"
                         message-key="soauthnt_salesforceOAuthView.hint.callbackURL"></div>
                    <div ng-messages="salesforceForm.callbackUrl.$error" ng-show="salesforceForm.callbackUrl.$invalid"
                         role="alert">
                        <div ng-message="url" message-key="error.notAValidURL"></div>
                    </div>
                </md-input-container>

                <div flex="5"></div>

                <md-input-container flex>
                    <label message-key="soauthnt_salesforceOAuthView.label.sandbox"></label>
                    <md-switch ng-model="salesforce.sandbox">
                        <span message-key="label.sandbox"></span>
                    </md-switch>
                </md-input-container>
            </div>
        </form>

        <md-card-actions layout="row" layout-align="end center">
            <md-button class="md-accent" message-key="label.mappers"
                       ng-click="salesforce.goToMappers()" ng-show="salesforce.connectorHasSettings">
            </md-button>
            <md-button class="md-accent" message-key="label.save"
                       ng-click="salesforce.saveSettings()">
            </md-button>
        </md-card-actions>

    </md-card-content>
</md-card>
