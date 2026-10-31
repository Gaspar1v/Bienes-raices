<%@ include file="comun/cabecera.jspf" %>
<h1>Pedidos de clientes</h1>
<table class="tabla">
    <thead><tr><th>Fecha</th><th>Operación</th><th>Tipo</th><th>Distrito</th><th>Precio máx. (S/)</th><th>m² mín.</th><th>Hab.</th><th>Contacto</th><th>Estado</th></tr></thead>
    <tbody>
    <c:forEach var="p" items="${pedidos}">
        <tr>
            <td><c:out value="${p.fechaTexto}"/></td>
            <td><c:out value="${p.tipoOperacion.etiqueta}"/></td>
            <td><c:out value="${empty p.tipoPropiedad ? 'Cualquiera' : p.tipoPropiedad.etiqueta}"/></td>
            <td><c:out value="${p.distrito}"/></td>
            <td class="num"><fmt:formatNumber value="${p.precioMaximo}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td class="num"><c:out value="${p.metrajeMinimo}"/></td>
            <td class="num"><c:out value="${p.habitacionesMinimas}"/></td>
            <td><c:out value="${p.nombreContacto}"/><br><small><c:out value="${p.email}"/> · <c:out value="${p.telefono}"/></small></td>
            <td><c:out value="${p.estado}"/></td>
        </tr>
    </c:forEach>
    <c:if test="${empty pedidos}"><tr><td colspan="9" class="tenue">No hay pedidos registrados.</td></tr></c:if>
    </tbody>
</table>
<%@ include file="comun/pie.jspf" %>
