<%@ include file="comun/cabecera.jspf" %>
<h1>Registrar pedido de compra o alquiler</h1>
<p>Déjanos lo que buscas y un agente te contactará cuando tengamos un inmueble que cumpla tus condiciones.</p>

<form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/pedido" novalidate>
    <input type="hidden" name="_csrf" value="${csrf}">
    <div class="rejilla">
        <label>Tipo de operación *
            <select name="operacion" required>
                <option value="">Seleccione</option>
                <c:forEach var="o" items="${tiposOperacion}">
                    <option value="${o}" ${pedido.tipoOperacion == o ? 'selected' : ''}><c:out value="${o.etiqueta}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.tipoOperacion}"><small class="error"><c:out value="${errores.tipoOperacion}"/></small></c:if>
        </label>
        <label>Tipo de propiedad
            <select name="tipo">
                <option value="">Cualquiera</option>
                <c:forEach var="t" items="${tiposPropiedad}">
                    <option value="${t}" ${pedido.tipoPropiedad == t ? 'selected' : ''}><c:out value="${t.etiqueta}"/></option>
                </c:forEach>
            </select>
        </label>
        <label>Distrito
            <input type="text" name="distrito" maxlength="60" value="<c:out value='${pedido.distrito}'/>">
        </label>
        <label>Precio máximo (S/)
            <input type="number" name="precioMax" min="0" step="0.01" value="<c:out value='${pedido.precioMaximo}'/>">
            <c:if test="${not empty errores.precioMaximo}"><small class="error"><c:out value="${errores.precioMaximo}"/></small></c:if>
        </label>
        <label>Metraje mínimo (m²)
            <input type="number" name="metrajeMin" min="0" step="0.01" value="<c:out value='${pedido.metrajeMinimo}'/>">
        </label>
        <label>Habitaciones mínimas
            <input type="number" name="habitaciones" min="0" max="50" value="<c:out value='${pedido.habitacionesMinimas}'/>">
        </label>
        <label>Nombre de contacto *
            <input type="text" name="nombreContacto" maxlength="100" required value="<c:out value='${pedido.nombreContacto}'/>">
            <c:if test="${not empty errores.nombreContacto}"><small class="error"><c:out value="${errores.nombreContacto}"/></small></c:if>
        </label>
        <label>Correo electrónico *
            <input type="email" name="email" maxlength="120" required value="<c:out value='${pedido.email}'/>">
            <c:if test="${not empty errores.email}"><small class="error"><c:out value="${errores.email}"/></small></c:if>
        </label>
        <label>Celular *
            <input type="tel" name="telefono" maxlength="9" required value="<c:out value='${pedido.telefono}'/>" placeholder="9XXXXXXXX">
            <c:if test="${not empty errores.telefono}"><small class="error"><c:out value="${errores.telefono}"/></small></c:if>
        </label>
    </div>
    <button type="submit" class="boton">Registrar pedido</button>
</form>
<%@ include file="comun/pie.jspf" %>
