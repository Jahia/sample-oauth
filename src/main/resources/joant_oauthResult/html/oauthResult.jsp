<%@ page language="java" contentType="text/html;charset=UTF-8" %>
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
<div class="oauth-result-header">
    <h2><fmt:message key="joant_oauthResult"/></h2>
</div>

<c:choose>
    <c:when test="${renderContext.liveMode}">
        <template:addResources>
            <script>
                (function() {
                    window.opener.postMessage({authenticationIsDone: true, isAuthenticate: ${param.isAuthenticate}, redirect:'${param.redirect}' }, '*');

                    <c:if test="${param.isAuthenticate eq true}">
                        console.log('This window will be closed in 3 sec');

                        var counter = 3;
                        setInterval(function () {
                            counter -= 1;
                            document.getElementById('count').innerHTML = counter;
                        }, 1000);
                    </c:if>
                })();
            </script>
        </template:addResources>

        <c:choose>
            <c:when test="${param.isAuthenticate}">
                <p>
                    <fmt:message key="joant_oauthResult.message.success.authenticate"/>
                </p>
            </c:when>
            <c:otherwise>
                <p class="error">
                    <fmt:message key="joant_oauthResult.message.error.authenticate"/>
                </p>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <p>
            <fmt:message key="joant_oauthResult.message.editMode"/>
        </p>
    </c:otherwise>
</c:choose>
