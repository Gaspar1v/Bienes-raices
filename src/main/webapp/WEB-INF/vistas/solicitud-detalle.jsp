<%@ include file="comun/cabecera.jspf" %>
<c:set var="s" value="${solicitud}"/>
<div class="titulo-accion">
    <h1>Solicitud N.° ${s.idSolicitud}: <c:out value="${s.tipo.etiqueta}"/> en <c:out value="${s.distrito}"/></h1>
    <a href="${pageContext.request.contextPath}/app/captacion">Volver a la lista</a>
</div>

<div class="dos-columnas">
    <section class="tarjeta">
        <h2>Datos del inmueble ofrecido</h2>
        <dl class="ficha">
            <dt>Estado</dt><dd><span class="estado SOL_${s.estado}"><c:out value="${s.estado.etiqueta}"/></span></dd>
            <dt>Operación</dt><dd><c:out value="${s.operacionOfrecida.etiqueta}"/></dd>
            <dt>Precio</dt><dd>S/ <fmt:formatNumber value="${s.precio}" minFractionDigits="2" maxFractionDigits="2"/></dd>
            <dt>Dirección</dt><dd><c:out value="${s.direccion}"/></dd>
            <dt>Metraje</dt><dd><c:out value="${s.metraje}"/> m²</dd>
            <dt>Habitaciones</dt><dd><c:out value="${s.habitaciones}"/></dd>
            <dt>Piso</dt><dd><c:out value="${s.piso}"/></dd>
            <dt>Cochera</dt><dd>${s.cochera ? 'Sí' : 'No'}</dd>
            <dt>Acabados</dt><dd><c:out value="${s.acabados}"/></dd>
            <dt>Referencias</dt><dd><c:out value="${s.referencias}"/></dd>
            <dt>Construcción</dt><dd><c:out value="${s.fechaConstruccion}"/></dd>
            <dt>Registrada</dt><dd><c:out value="${s.fechaRegistroTexto}"/></dd>
        </dl>
    </section>
    <section class="tarjeta">
        <h2>Propietario</h2>
        <dl class="ficha">
            <dt>Nombres</dt><dd><c:out value="${s.propietarioNombres}"/></dd>
            <dt>DNI</dt><dd><c:out value="${s.propietarioDni}"/></dd>
            <dt>Celular</dt><dd><c:out value="${s.propietarioTelefono}"/></dd>
            <dt>Correo</dt><dd><c:out value="${s.propietarioEmail}"/></dd>
        </dl>
        <h2>Verificación de documentación</h2>
        <c:if test="${not empty s.revisor}">
            <p class="tenue">Revisado por <c:out value="${s.revisor}"/> el <c:out value="${s.fechaRevisionTexto}"/></p>
        </c:if>
        <c:if test="${not empty s.observaciones}">
            <div class="alerta aviso"><strong>Observaciones:</strong> <c:out value="${s.observaciones}"/></div>
        </c:if>
        <c:choose>
            <c:when test="${s.estado.revisable}">
                <form method="post" action="${pageContext.request.contextPath}/app/captacion" class="formulario">
                    <input type="hidden" name="_csrf" value="${csrf}">
                    <input type="hidden" name="id" value="${s.idSolicitud}">
                    <label>Observaciones de la documentación
                        <textarea name="observaciones" maxlength="300" rows="3"></textarea>
                    </label>
                    <div class="en-fila">
                        <button type="submit" name="accion" value="observar" class="boton-borde">Registrar observaciones</button>
                        <button type="submit" name="accion" value="aceptar" class="boton">Aceptar e incorporar a la cartera</button>
                    </div>
                </form>
            </c:when>
            <c:otherwise>
                <p>La solicitud fue aceptada.
                    <a href="${pageContext.request.contextPath}/app/inmuebles?accion=ver&amp;id=${s.idInmueble}">Ver inmueble N.° ${s.idInmueble}</a></p>
            </c:otherwise>
        </c:choose>
    </section>
</div>
<%@ include file="comun/pie.jspf" %>
