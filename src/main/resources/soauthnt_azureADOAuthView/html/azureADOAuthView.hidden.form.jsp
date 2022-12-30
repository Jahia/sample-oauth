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
<div layout="row">
    <md-input-container flex>
        <label message-key="label.host"></label>
        <input type="text" ng-model="${connectorVar}.host" name="host" required/>
        <div ng-messages="${connectorFormVar}.host.$error" role="alert">
            <div ng-message="required" message-key="error.host.required"></div>
        </div>
    </md-input-container>
</div>
<div layout="row">
    <md-input-container flex>
        <label message-key="label.apiKey"></label>
        <input type="text" ng-model="${connectorVar}.apiKey" name="apiKey" required/>
        <div ng-messages="${connectorFormVar}.apiKey.$error" role="alert">
            <div ng-message="required" message-key="error.apiKey.required"></div>
        </div>
    </md-input-container>

    <div flex="5"></div>

    <md-input-container flex>
        <label message-key="label.apiSecret"></label>
        <input type="text" ng-model="${connectorVar}.apiSecret" name="apiSecret" required/>
        <div ng-messages="${connectorFormVar}.apiSecret.$error" role="alert">
            <div ng-message="required" message-key="error.apiSecret.required"></div>
        </div>
    </md-input-container>
</div>

<div layout="row">
    <md-input-container flex>
        <label message-key="label.scope"></label>
        <input type="text" ng-model="${connectorVar}.scope" name="scope"/>
        <div class="hint" ng-show="!${connectorFormVar}.scope.$invalid"
             message-key="hint.scope"></div>
        <div ng-messages="${connectorFormVar}.scope.$error" role="alert">
            <div ng-message="required" message-key="error.scope.required"></div>
        </div>
    </md-input-container>

    <div flex="5"></div>

    <md-input-container class="md-block" flex>
        <label message-key="soauthnt_azureADOAuthView.label.tenantID"></label>
        <input type="text" ng-model="${connectorVar}.tenantID" name="tenantID"/>
        <div ng-messages="${connectorFormVar}.tenantID.$error" ng-show="${connectorFormVar}.tenantID.$invalid"
             role="alert">
            <div ng-message="required" message-key="soauthnt_azureADOAuthView.error.realm.required"></div>
        </div>
    </md-input-container>
</div>

<div layout="row">
    <md-input-container class="md-block" flex>
        <label message-key="label.callbackURL"></label>
        <input type="url" ng-model="${connectorVar}.callbackUrl" name="callbackUrl"/>
        <div class="hint" ng-show="${connectorFormVar}.callbackUrl.$valid"
             message-key="soauthnt_azureADOAuthView.hint.callbackURL"></div>
        <div ng-messages="${connectorFormVar}.callbackUrl.$error" ng-show="${connectorFormVar}.callbackUrl.$invalid"
             role="alert">
            <div ng-message="url" message-key="error.notAValidURL"></div>
        </div>
    </md-input-container>
</div>
