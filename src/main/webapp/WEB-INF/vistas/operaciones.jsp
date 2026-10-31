<%@ include file="comun/cabecera.jspf" %>
<h1>Registrar operación de venta o alquiler</h1>
<c:if test="${not empty errores}">
    <div class="alerta peligro" role="alert">
        <c:out value="${empty errores.idInmueble ? 'Revise los campos marcados.' : errores.idInmueble}"/>
    </div>
</c:if>

<form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/app/operaciones" novalidate>
    <input type="hidden" name="_csrf" value="${csrf}">
    <div class="rejilla">
        <label class="doble">Inmueble disponible *
            <select name="idInmueble" required>
                <option value="0">Seleccione</option>
                <c:forEach var="i" items="${disponibles}">
                    <option value="${i.idInmueble}" ${operacion.idInmueble == i.idInmueble ? 'selected' : ''}>
                        #${i.idInmueble} · <c:out value="${i.tipo.etiqueta}"/> en <c:out value="${i.distrito}"/> · <c:out value="${i.operacionOfrecida.etiqueta}"/> · S/ <fmt:formatNumber value="${i.precio}" minFractionDigits="2"/>
                    </option>
                </c:forEach>
            </select>
        </label>
        <label>Tipo *
            <select name="tipo" required>
                <option value="">Seleccione</option>
                <c:forEach var="o" items="${tiposOperacion}">
                    <option value="${o}" ${operacion.tipo == o ? 'selected' : ''}><c:out value="${o.etiqueta}"/></option>
                </c:forEach>
            </select>
            <c:if test="${not empty errores.tipo}"><small class="error"><c:out value="${errores.tipo}"/></small></c:if>
        </label>
        <label>Comprador / inquilino *
            <input type="text" name="clienteNombre" maxlength="100" required value="<c:out value='${operacion.clienteNombre}'/>">
            <c:if test="${not empty errores.clienteNombre}"><small class="error"><c:out value="${errores.clienteNombre}"/></small></c:if>
        </label>
        <label>DNI del cliente *
            <input type="text" name="clienteDni" maxlength="8" required value="<c:out value='${operacion.clienteDni}'/>">
            <c:if test="${not empty errores.clienteDni}"><small class="error"><c:out value="${errores.clienteDni}"/></small></c:if>
        </label>
        <label>Monto (venta) o renta mensual (S/) *
            <input type="number" name="monto" min="0" step="0.01" required value="<c:out value='${operacion.monto}'/>">
            <c:if test="${not empty errores.monto}"><small class="error"><c:out value="${errores.monto}"/></small></c:if>
        </label>
        <label>Fecha de inicio / contrato *
            <input type="date" name="fechaInicio" required value="<c:out value='${operacion.fechaInicio}'/>">
            <c:if test="${not empty errores.fechaInicio}"><small class="error"><c:out value="${errores.fechaInicio}"/></small></c:if>
        </label>
        <label>Fecha de fin (solo alquiler)
            <input type="date" name="fechaFin" value="<c:out value='${operacion.fechaFin}'/>">
            <c:if test="${not empty errores.fechaFin}"><small class="error"><c:out value="${errores.fechaFin}"/></small></c:if>
        </label>
    </div>
    <p class="tenue">Comisión automática: venta 3 % del precio; alquiler un mes de renta. El inmueble pasará a «Vendido» o «Alquilado».</p>
    <button type="submit" class="boton">Registrar operación</button>
</form>

<h2>Operaciones del mes</h2>
<table class="tabla">
    <thead><tr><th>ID</th><th>Tipo</th><th>Inmueble</th><th>Cliente</th><th>Agente</th><th>Inicio</th><th>Monto (S/)</th><th>Comisión (S/)</th><th>Contrato</th></tr></thead>
    <tbody>
    <c:forEach var="o" items="${operaciones}">
        <tr>
            <td>${o.idOperacion}</td>
            <td><c:out value="${o.tipo.etiqueta}"/></td>
            <td><c:out value="${o.descripcionInmueble}"/></td>
            <td><c:out value="${o.clienteNombre}"/></td>
            <td><c:out value="${o.nombreAgente}"/></td>
            <td><c:out value="${o.fechaInicio}"/></td>
            <td class="num"><fmt:formatNumber value="${o.monto}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td class="num"><fmt:formatNumber value="${o.comision}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td><a href="${pageContext.request.contextPath}/app/operaciones?contrato=${o.idOperacion}">PDF</a></td>
        </tr>
    </c:forEach>
    <c:if test="${empty operaciones}"><tr><td colspan="9" class="tenue">Sin operaciones este mes.</td></tr></c:if>
    </tbody>
</table>
<%@ include file="comun/pie.jspf" %>
