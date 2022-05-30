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
<c:choose>
    <c:when test="${renderContext.editMode}">
        ${currentNode.displayableName}
    </c:when>
    <c:otherwise>
        <c:if test="${not renderContext.loggedIn}">
            <p>Technical Connectors:</p>
            <dl>
                <c:forEach items="${requestScope.technicalConnectors}" var="connector">
                    <dt>${connector}</dt>
                    <dd>
                        <c:url var="href" value="${url.base}${renderContext.site.home.path}.connectTo${connector}Action.do"/>
                        <c:url var="src" value="${url.currentModule}/img/${connector}.svg"/>
                        <a href="${href}"><img alt="${connector}" src="${src}"/></a>
                    </dd>
                </c:forEach>
            </dl>

            <p>Non Technical Connectors:</p>
            <dl>
                <c:forEach items="${requestScope.nonTechnicalConnectors}" var="connector">
                    <dt>${connector}</dt>
                    <dd>
                        <c:url var="href" value="${url.base}${renderContext.site.home.path}.connectTo${connector}Action.do"/>
                        <c:url var="src" value="${url.currentModule}/img/${connector}.svg"/>
                        <a href="${href}"><img alt="${connector}" src="${src}"/></a>
                    </dd>
                </c:forEach>
            </dl>
        </c:if>
    </c:otherwise>
</c:choose>
