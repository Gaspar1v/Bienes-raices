<%@ page isErrorPage="true" %>
<%@ include file="comun/cabecera.jspf" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<section class="tarjeta acceso">
    <h1>
        <c:choose>
            <c:when test="${codigo == 403}">Acceso no permitido</c:when>
            <c:when test="${codigo == 404}">Página no encontrada</c:when>
            <c:otherwise>Ocurrió un error</c:otherwise>
        </c:choose>
    </h1>
    <p>
        <c:choose>
            <c:when test="${codigo == 403}">No tiene permisos para realizar esta acción o la solicitud no es válida.</c:when>
            <c:when test="${codigo == 404}">El recurso solicitado no existe.</c:when>
            <c:otherwise>No pudimos completar la operación. El error quedó registrado para su revisión.</c:otherwise>
        </c:choose>
    </p>
    <a class="boton" href="${pageContext.request.contextPath}/app/panel">Volver al panel</a>
</section>
<%@ include file="comun/pie.jspf" %>
