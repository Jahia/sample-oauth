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
<c:if test="${renderContext.editMode}">${currentNode.identifier}</c:if>
<c:if test="${not renderContext.editMode && !renderContext.loggedIn}">
    <template:addResources type="javascript" resources="${requestScope['auth.url']}/auth/js/keycloak.js"/>
    <template:addResources type="inlinejavascript">
        <script>
            document.addEventListener("DOMContentLoaded", () => {
                Keycloak({
                    url: '${requestScope['auth.url']}/auth',
                    realm: '${requestScope['auth.realm']}',
                    clientId: '${requestScope['auth.clientId']}'
                }).init({onLoad: 'check-sso', checkLoginIframe: false})
                    .then(authenticated => {
                        if (authenticated) {
                            fetch('<c:url value="${url.base}${renderContext.site.home.path}"/>.connectToKeycloakAction.do', {
                                headers: {Accept: 'application/json'}
                            }).then(response => response.json())
                                .then(data => location.replace(data.authorizationUrl))
                                .catch(e => console.log(e));
                        }
                    }).catch(e => console.log(e));
            });
        </script>
    </template:addResources>
</c:if>
