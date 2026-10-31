<%@ include file="comun/cabecera.jspf" %>
<c:set var="i" value="${inmueble}"/>
<div class="titulo-accion">
    <h1>Inmueble #${i.idInmueble}: <c:out value="${i.tipo.etiqueta}"/> en <c:out value="${i.distrito}"/></h1>
    <c:if test="${puedeEditar}">
        <a class="boton" href="${pageContext.request.contextPath}/app/inmuebles?accion=editar&amp;id=${i.idInmueble}">Modificar</a>
    </c:if>
</div>

<div class="dos-columnas">
    <section class="tarjeta">
        <h2>Ficha completa</h2>
        <dl class="ficha">
            <dt>Estado</dt><dd><span class="estado ${i.estado}"><c:out value="${i.estado.etiqueta}"/></span></dd>
            <dt>Se ofrece en</dt><dd><c:out value="${i.operacionOfrecida.etiqueta}"/></dd>
            <dt>Precio</dt><dd>S/ <fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/></dd>
            <dt>Dirección</dt><dd><c:out value="${i.direccion}"/></dd>
            <dt>Metraje</dt><dd><c:out value="${i.metraje}"/> m²</dd>
            <dt>Habitaciones</dt><dd><c:out value="${i.habitaciones}"/></dd>
            <dt>Piso</dt><dd><c:out value="${i.piso}"/></dd>
            <dt>Cochera</dt><dd>${i.cochera ? 'Sí' : 'No'}</dd>
            <dt>Acabados</dt><dd><c:out value="${i.acabados}"/></dd>
            <dt>Referencias</dt><dd><c:out value="${i.referencias}"/></dd>
            <dt>Construcción</dt><dd><c:out value="${i.fechaConstruccion}"/></dd>
            <dt>Propietario</dt><dd><c:out value="${i.nombrePropietario}"/></dd>
        </dl>
        <c:if test="${i.estado.disponible and sessionScope.usuario.rol != 'GERENTE'}">
            <a class="boton" href="${pageContext.request.contextPath}/app/operaciones?idInmueble=${i.idInmueble}">Registrar venta / alquiler</a>
        </c:if>
    </section>

    <section class="tarjeta">
        <c:if test="${puedeEditar}">
            <h2>Actualizar estado</h2>
            <form method="post" action="${pageContext.request.contextPath}/app/inmuebles" class="en-fila">
                <input type="hidden" name="_csrf" value="${csrf}">
                <input type="hidden" name="accion" value="estado">
                <input type="hidden" name="id" value="${i.idInmueble}">
                <select name="estado" aria-label="Nuevo estado">
                    <c:forEach var="e" items="${estados}">
                        <option value="${e}" ${i.estado == e ? 'selected' : ''}><c:out value="${e.etiqueta}"/></option>
                    </c:forEach>
                </select>
                <button type="submit" class="boton">Guardar estado</button>
            </form>
        </c:if>
        <h2>Historial de cambios</h2>
        <table class="tabla compacta">
            <thead><tr><th>Fecha</th><th>Anterior</th><th>Nuevo</th><th>Usuario</th></tr></thead>
            <tbody>
            <c:forEach var="h" items="${historial}">
                <tr>
                    <td><c:out value="${h.fechaTexto}"/></td>
                    <td><c:out value="${empty h.estadoAnterior ? '—' : h.estadoAnterior.etiqueta}"/></td>
                    <td><c:out value="${h.estadoNuevo.etiqueta}"/></td>
                    <td><c:out value="${h.usuario}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </section>
</div>
<%@ include file="comun/pie.jspf" %>
