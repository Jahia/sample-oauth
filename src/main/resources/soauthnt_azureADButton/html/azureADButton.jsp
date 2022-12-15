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
        function connectToAzureAD${fn:replace(currentNode.identifier, '-', '')}() {
            var popup = window.open('', "Azure AD Authorization", "menubar=no,status=no,scrollbars=no,width=1145,height=725,modal=yes,alwaysRaised=yes");
            var xhr = new XMLHttpRequest();
            xhr.open('GET', '<c:url value="${url.base}${renderContext.mainResource.node.path}"/>.connectToAzureADAction.do');
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
                                window.location.search = 'site=${renderContext.site.siteKey}&redirect=' + event.data.redirect;
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
                <c:if test="${not renderContext.editMode}"> onclick="connectToAzureAD${fn:replace(currentNode.identifier, '-', '')}()" </c:if>
                <c:if test="${not empty htmlId}"> id="${htmlId}"</c:if>
                <c:if test="${renderContext.editMode}">disabled</c:if> >
                <span class="custom-icon-svg">
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                         viewBox="0 0 48 48"><defs><path id="a"
                                                         d="M44.5 20H24v8.5h11.8C34.7 33.9 30.1 37 24 37c-7.2 0-13-5.8-13-13s5.8-13 13-13c3.1 0 5.9 1.1 8.1 2.9l6.4-6.4C34.6 4.1 29.6 2 24 2 11.8 2 2 11.8 2 24s9.8 22 22 22c11 0 21-8 21-22 0-1.3-.2-2.7-.5-4z"/></defs><clipPath
                            id="b"><use xlink:href="#a" overflow="visible"/></clipPath><path clip-path="url(#b)"
                                                                                             fill="#FBBC05"
                                                                                             d="M0 37V11l17 13z"/><path
                            clip-path="url(#b)" fill="#EA4335" d="M0 11l17 13 7-6.1L48 14V0H0z"/><path
                            clip-path="url(#b)" fill="#34A853" d="M0 37l30-23 7.9 1L48 0v48H0z"/><path
                            clip-path="url(#b)" fill="#4285F4" d="M48 48L17 24l-4-3 35-10z"/></svg>
                </span>
            <p class="btn-text">${currentNode.displayableName}</p>
        </button>
    </c:when>
    <c:otherwise>
        <a href="#" class="google-btn custom-btn-theme ${cssClass}"
                <c:if test="${not renderContext.editMode}"> onclick="connectToAzureAD${fn:replace(currentNode.identifier, '-', '')}();return false;" </c:if>
                <c:if test="${not empty htmlId}"> id="${htmlId}"</c:if>
           <c:if test="${renderContext.editMode}">disabled</c:if> >
                <span class="custom-icon-svg">
                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                         viewBox="0 0 48 48"><defs><path id="a"
                                                         d="M44.5 20H24v8.5h11.8C34.7 33.9 30.1 37 24 37c-7.2 0-13-5.8-13-13s5.8-13 13-13c3.1 0 5.9 1.1 8.1 2.9l6.4-6.4C34.6 4.1 29.6 2 24 2 11.8 2 2 11.8 2 24s9.8 22 22 22c11 0 21-8 21-22 0-1.3-.2-2.7-.5-4z"/></defs><clipPath
                            id="b"><use xlink:href="#a" overflow="visible"/></clipPath><path clip-path="url(#b)"
                                                                                             fill="#FBBC05"
                                                                                             d="M0 37V11l17 13z"/><path
                            clip-path="url(#b)" fill="#EA4335" d="M0 11l17 13 7-6.1L48 14V0H0z"/><path
                            clip-path="url(#b)" fill="#34A853" d="M0 37l30-23 7.9 1L48 0v48H0z"/><path
                            clip-path="url(#b)" fill="#4285F4" d="M48 48L17 24l-4-3 35-10z"/></svg>
                </span>
            <p class="btn-text">${currentNode.displayableName}</p>
        </a>
    </c:otherwise>
</c:choose>
