<%@ include file="comun/cabecera.jspf" %>
<h1><c:out value="${inmueble.idInmueble == 0 ? 'Registrar inmueble' : 'Modificar inmueble'}"/></h1>
<c:if test="${not empty errores}">
    <div class="alerta peligro" role="alert">Revise los campos marcados.</div>
</c:if>

<form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/app/inmuebles" novalidate>
    <input type="hidden" name="_csrf" value="${csrf}">
    <input type="hidden" name="idInmueble" value="${inmueble.idInmueble}">
    <div class="rejilla">
        <label>Tipo de propiedad *
            <select name="tipo" required>
                <option value="">Seleccione</option>
                <c:forEach var="t" items="${tiposPropiedad}">
                    <option value="${t}" ${inmueble.tipo == t ? 'selected' : ''}><c:out value="${t.etiqueta}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.tipo}"><small class="error"><c:out value="${errores.tipo}"/></small></c:if>
        </label>
        <label>Se ofrece en *
            <select name="operacionOfrecida" required>
                <option value="">Seleccione</option>
                <c:forEach var="o" items="${tiposOperacion}">
                    <option value="${o}" ${inmueble.operacionOfrecida == o ? 'selected' : ''}><c:out value="${o.etiqueta}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.operacionOfrecida}"><small class="error"><c:out value="${errores.operacionOfrecida}"/></small></c:if>
        </label>
        <label>Propietario *
            <select name="idPropietario" required>
                <option value="0">Seleccione</option>
                <c:forEach var="p" items="${propietarios}">
                    <option value="${p.idPropietario}" ${inmueble.idPropietario == p.idPropietario ? 'selected' : ''}><c:out value="${p.nombres}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.idPropietario}"><small class="error"><c:out value="${errores.idPropietario}"/></small></c:if>
        </label>
        <label>Metraje (m²) *
            <input type="number" name="metraje" min="0" step="0.01" required value="<c:out value='${inmueble.metraje}'/>">
            <c:if test="${not empty errores.metraje}"><small class="error"><c:out value="${errores.metraje}"/></small></c:if>
        </label>
        <label>Precio (S/) *
            <input type="number" name="precio" min="0" step="0.01" required value="<c:out value='${inmueble.precio}'/>">
            <c:if test="${not empty errores.precio}"><small class="error"><c:out value="${errores.precio}"/></small></c:if>
        </label>
        <label>Distrito *
            <input type="text" name="distrito" maxlength="60" required value="<c:out value='${inmueble.distrito}'/>">
            <c:if test="${not empty errores.distrito}"><small class="error"><c:out value="${errores.distrito}"/></small></c:if>
        </label>
        <label class="doble">Dirección *
            <input type="text" name="direccion" maxlength="150" required value="<c:out value='${inmueble.direccion}'/>">
            <c:if test="${not empty errores.direccion}"><small class="error"><c:out value="${errores.direccion}"/></small></c:if>
        </label>
        <label>Habitaciones
            <input type="number" name="habitaciones" min="0" max="50" value="<c:out value='${inmueble.habitaciones}'/>">
            <c:if test="${not empty errores.habitaciones}"><small class="error"><c:out value="${errores.habitaciones}"/></small></c:if>
        </label>
        <label>Piso
            <input type="number" name="piso" min="-5" max="80" value="<c:out value='${inmueble.piso}'/>">
            <c:if test="${not empty errores.piso}"><small class="error"><c:out value="${errores.piso}"/></small></c:if>
        </label>
        <label>Fecha de construcción
            <input type="date" name="fechaConstruccion" value="<c:out value='${inmueble.fechaConstruccion}'/>">
            <c:if test="${not empty errores.fechaConstruccion}"><small class="error"><c:out value="${errores.fechaConstruccion}"/></small></c:if>
        </label>
        <label class="casilla"><input type="checkbox" name="cochera" ${inmueble.cochera ? 'checked' : ''}> Tiene cochera</label>
        <label class="doble">Acabados
            <input type="text" name="acabados" maxlength="200" value="<c:out value='${inmueble.acabados}'/>">
        </label>
        <label class="doble">Referencias
            <input type="text" name="referencias" maxlength="300" value="<c:out value='${inmueble.referencias}'/>">
        </label>
    </div>
    <button type="submit" class="boton">Guardar</button>
    <a href="${pageContext.request.contextPath}/app/inmuebles">Cancelar</a>
</form>
<%@ include file="comun/pie.jspf" %>
