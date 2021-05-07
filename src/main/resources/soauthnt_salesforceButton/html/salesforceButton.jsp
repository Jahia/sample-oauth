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
<c:set var="cssClass" value="${currentNode.properties['cssClass'].string}"/>
<c:set var="htmlId" value="${currentNode.properties['htmlId'].string}"/>
<c:set var="tagType" value="${currentNode.properties['tagType'].string}"/>

<template:addResources>
    <script>
        function connectToSalesforce${fn:replace(currentNode.identifier, '-', '')}() {
            var popup = window.open('', "Salesforce Authorization", "menubar=no,status=no,scrollbars=no,width=1145,height=725,modal=yes,alwaysRaised=yes");
            var xhr = new XMLHttpRequest();
            xhr.open('GET', '<c:url value="${url.base}${renderContext.site.home.path}"/>.connectToSalesforceAction.do');
            xhr.setRequestHeader('Accept', 'application/json;');
            xhr.onreadystatechange = () => {
                if (xhr.readyState !== 4 || xhr.status !== 200) {
                    return;
                }
                popup.location.href = JSON.parse(xhr.responseText).authorizationUrl;
                window.addEventListener('message', event => {
                    if (event.data.authenticationIsDone) {
                        setTimeout(() => {
                            popup.close();
                            if (event.data.isAuthenticate) {
                                window.location.search = 'site=${renderContext.site.siteKey}';
                            }
                        }, 3000);
                    }
                });
            };
            xhr.send();
        }
    </script>
</template:addResources>

<c:choose>
    <c:when test="${tagType eq 'button'}">
        <button class="google-btn custom-btn-theme ${cssClass}" type="button"
                <c:if test="${not renderContext.editMode}"> onclick="connectToSalesforce${fn:replace(currentNode.identifier, '-', '')}()" </c:if>
                <c:if test="${not empty htmlId}"> id="${htmlId}"</c:if>
                <c:if test="${renderContext.editMode}">disabled</c:if> >
            <p class="btn-text">${currentNode.displayableName}</p>
        </button>
    </c:when>
    <c:otherwise>
        <a href="#" class="google-btn custom-btn-theme ${cssClass}"
                <c:if test="${not renderContext.editMode}"> onclick="connectToSalesforce${fn:replace(currentNode.identifier, '-', '')}();return false;" </c:if>
                <c:if test="${not empty htmlId}"> id="${htmlId}"</c:if>
           <c:if test="${renderContext.editMode}">disabled</c:if> >
            <p class="btn-text">${currentNode.displayableName}</p>
        </a>
    </c:otherwise>
</c:choose>
