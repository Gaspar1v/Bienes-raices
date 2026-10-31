<%@ include file="comun/cabecera.jspf" %>
<h1>Solicitudes de captación</h1>

<form class="tarjeta filtros en-fila" method="get" action="${pageContext.request.contextPath}/app/captacion">
    <select name="estado" aria-label="Estado">
        <c:forEach var="e" items="${estadosSolicitud}">
            <option value="${e}" ${filtro == e.name() ? 'selected' : ''}><c:out value="${e.etiqueta}"/></option>
        </c:forEach>
        <option value="TODAS" ${filtro == 'TODAS' ? 'selected' : ''}>Todas</option>
    </select>
    <button type="submit" class="boton">Filtrar</button>
</form>

<table class="tabla">
    <thead><tr><th>N.°</th><th>Fecha</th><th>Inmueble</th><th>Operación</th><th>Precio (S/)</th><th>Propietario</th><th>Estado</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${solicitudes}">
        <tr>
            <td>${s.idSolicitud}</td>
            <td><c:out value="${s.fechaRegistroTexto}"/></td>
            <td><c:out value="${s.tipo.etiqueta}"/> en <c:out value="${s.distrito}"/></td>
            <td><c:out value="${s.operacionOfrecida.etiqueta}"/></td>
            <td class="num"><fmt:formatNumber value="${s.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td><c:out value="${s.propietarioNombres}"/></td>
            <td><span class="estado SOL_${s.estado}"><c:out value="${s.estado.etiqueta}"/></span></td>
            <td><a href="${pageContext.request.contextPath}/app/captacion?id=${s.idSolicitud}">Revisar</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty solicitudes}"><tr><td colspan="8" class="tenue">No hay solicitudes en este estado.</td></tr></c:if>
    </tbody>
</table>
<%@ include file="comun/pie.jspf" %>
