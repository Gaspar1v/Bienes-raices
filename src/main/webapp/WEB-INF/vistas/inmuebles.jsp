<%@ include file="comun/cabecera.jspf" %>
<div class="titulo-accion">
    <h1>Cartera de inmuebles</h1>
    <c:if test="${puedeEditar}">
        <a class="boton" href="${pageContext.request.contextPath}/app/inmuebles?accion=nuevo">+ Registrar inmueble</a>
    </c:if>
</div>

<form class="tarjeta filtros en-fila" method="get" action="${pageContext.request.contextPath}/app/inmuebles">
    <select name="operacion" aria-label="Operación">
        <option value="">Venta y alquiler</option>
        <c:forEach var="o" items="${tiposOperacion}">
            <option value="${o}" ${criterio.operacion == o ? 'selected' : ''}><c:out value="${o.etiqueta}"/></option>
        </c:forEach>
    </select>
    <select name="tipo" aria-label="Tipo">
        <option value="">Todos los tipos</option>
        <c:forEach var="t" items="${tiposPropiedad}">
            <option value="${t}" ${criterio.tipo == t ? 'selected' : ''}><c:out value="${t.etiqueta}"/></option>
        </c:forEach>
    </select>
    <input type="text" name="distrito" placeholder="Distrito" maxlength="60" value="<c:out value='${criterio.distrito}'/>">
    <label class="casilla"><input type="checkbox" name="soloDisponibles" ${criterio.soloDisponibles ? 'checked' : ''}> Solo disponibles</label>
    <button type="submit" class="boton">Filtrar</button>
</form>

<table class="tabla">
    <thead>
    <tr><th>ID</th><th>Tipo</th><th>Operación</th><th>Distrito</th><th>Precio (S/)</th><th>m²</th><th>Propietario</th><th>Estado</th><th></th></tr>
    </thead>
    <tbody>
    <c:forEach var="i" items="${inmuebles}">
        <tr>
            <td>${i.idInmueble}</td>
            <td><c:out value="${i.tipo.etiqueta}"/></td>
            <td><c:out value="${i.operacionOfrecida.etiqueta}"/></td>
            <td><c:out value="${i.distrito}"/></td>
            <td class="num"><fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td class="num"><c:out value="${i.metraje}"/></td>
            <td><c:out value="${i.nombrePropietario}"/></td>
            <td><span class="estado ${i.estado}"><c:out value="${i.estado.etiqueta}"/></span></td>
            <td><a href="${pageContext.request.contextPath}/app/inmuebles?accion=ver&amp;id=${i.idInmueble}">Ver ficha</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty inmuebles}">
        <tr><td colspan="9" class="tenue">No existen coincidencias.</td></tr>
    </c:if>
    </tbody>
</table>
<%@ include file="comun/pie.jspf" %>
