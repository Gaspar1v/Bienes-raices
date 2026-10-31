<%@ include file="comun/cabecera.jspf" %>
<c:set var="s" value="${solicitud}"/>
<h1>Ofrecer mi inmueble</h1>
<p>Complete los datos de su propiedad. Un agente revisará la información y la documentación antes de publicarla en la cartera.</p>
<c:if test="${not empty errores}">
    <div class="alerta peligro" role="alert">Revise los campos marcados.</div>
</c:if>

<form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/captacion" novalidate>
    <input type="hidden" name="_csrf" value="${csrf}">
    <h2>Datos del inmueble</h2>
    <div class="rejilla">
        <label>Deseo *
            <select name="operacionOfrecida" required>
                <option value="">Seleccione</option>
                <c:forEach var="o" items="${tiposOperacion}">
                    <option value="${o}" ${s.operacionOfrecida == o ? 'selected' : ''}><c:out value="${o == 'VENTA' ? 'Vender' : 'Alquilar'}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.operacionOfrecida}"><small class="error"><c:out value="${errores.operacionOfrecida}"/></small></c:if>
        </label>
        <label>Tipo de propiedad *
            <select name="tipo" required>
                <option value="">Seleccione</option>
                <c:forEach var="t" items="${tiposPropiedad}">
                    <option value="${t}" ${s.tipo == t ? 'selected' : ''}><c:out value="${t.etiqueta}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.tipo}"><small class="error"><c:out value="${errores.tipo}"/></small></c:if>
        </label>
        <label>Metraje (m²) *
            <input type="number" name="metraje" min="0" step="0.01" required value="<c:out value='${s.metraje}'/>">
            <c:if test="${not empty errores.metraje}"><small class="error"><c:out value="${errores.metraje}"/></small></c:if>
        </label>
        <label>Precio solicitado (S/) *
            <input type="number" name="precio" min="0" step="0.01" required value="<c:out value='${s.precio}'/>">
            <c:if test="${not empty errores.precio}"><small class="error"><c:out value="${errores.precio}"/></small></c:if>
        </label>
        <label>Distrito *
            <input type="text" name="distrito" maxlength="60" required value="<c:out value='${s.distrito}'/>">
            <c:if test="${not empty errores.distrito}"><small class="error"><c:out value="${errores.distrito}"/></small></c:if>
        </label>
        <label class="doble">Dirección *
            <input type="text" name="direccion" maxlength="150" required value="<c:out value='${s.direccion}'/>">
            <c:if test="${not empty errores.direccion}"><small class="error"><c:out value="${errores.direccion}"/></small></c:if>
        </label>
        <label>Habitaciones
            <input type="number" name="habitaciones" min="0" max="50" value="<c:out value='${s.habitaciones}'/>">
            <c:if test="${not empty errores.habitaciones}"><small class="error"><c:out value="${errores.habitaciones}"/></small></c:if>
        </label>
        <label>Piso
            <input type="number" name="piso" min="-5" max="80" value="<c:out value='${s.piso}'/>">
            <c:if test="${not empty errores.piso}"><small class="error"><c:out value="${errores.piso}"/></small></c:if>
        </label>
        <label>Fecha de construcción
            <input type="date" name="fechaConstruccion" value="<c:out value='${s.fechaConstruccion}'/>">
            <c:if test="${not empty errores.fechaConstruccion}"><small class="error"><c:out value="${errores.fechaConstruccion}"/></small></c:if>
        </label>
        <label class="casilla"><input type="checkbox" name="cochera" ${s.cochera ? 'checked' : ''}> Tiene cochera</label>
        <label class="doble">Acabados
            <input type="text" name="acabados" maxlength="200" value="<c:out value='${s.acabados}'/>">
        </label>
        <label class="doble">Referencias
            <input type="text" name="referencias" maxlength="300" value="<c:out value='${s.referencias}'/>">
        </label>
    </div>

    <h2>Datos de contacto del propietario</h2>
    <div class="rejilla">
        <label class="doble">Nombres y apellidos *
            <input type="text" name="propietarioNombres" maxlength="100" required value="<c:out value='${s.propietarioNombres}'/>">
            <c:if test="${not empty errores.propietarioNombres}"><small class="error"><c:out value="${errores.propietarioNombres}"/></small></c:if>
        </label>
        <label>DNI *
            <input type="text" name="propietarioDni" maxlength="8" required value="<c:out value='${s.propietarioDni}'/>">
            <c:if test="${not empty errores.propietarioDni}"><small class="error"><c:out value="${errores.propietarioDni}"/></small></c:if>
        </label>
        <label>Celular *
            <input type="tel" name="propietarioTelefono" maxlength="9" required placeholder="9XXXXXXXX" value="<c:out value='${s.propietarioTelefono}'/>">
            <c:if test="${not empty errores.propietarioTelefono}"><small class="error"><c:out value="${errores.propietarioTelefono}"/></small></c:if>
        </label>
        <label class="doble">Correo electrónico *
            <input type="email" name="propietarioEmail" maxlength="120" required value="<c:out value='${s.propietarioEmail}'/>">
            <c:if test="${not empty errores.propietarioEmail}"><small class="error"><c:out value="${errores.propietarioEmail}"/></small></c:if>
        </label>
    </div>
    <button type="submit" class="boton">Enviar solicitud</button>
</form>
<%@ include file="comun/pie.jspf" %>
