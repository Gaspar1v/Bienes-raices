<%@ include file="comun/cabecera.jspf" %>
<section class="portada">
    <h1>Encuentra tu próximo hogar en Arequipa</h1>
    <p>Casas, departamentos, locales, oficinas y terrenos en venta y alquiler.</p>
    <p><a class="boton-borde" href="${pageContext.request.contextPath}/captacion">¿Quieres vender o alquilar tu inmueble? Ofrécelo aquí</a></p>
</section>

<form class="tarjeta filtros" method="get" action="${pageContext.request.contextPath}/inicio">
    <div class="opciones-operacion">
        <label><input type="radio" name="operacion" value="" ${empty criterio.operacion ? 'checked' : ''}> Todas</label>
        <label><input type="radio" name="operacion" value="VENTA" ${criterio.operacion == 'VENTA' ? 'checked' : ''}> Comprar</label>
        <label><input type="radio" name="operacion" value="ALQUILER" ${criterio.operacion == 'ALQUILER' ? 'checked' : ''}> Alquilar</label>
    </div>
    <div class="rejilla">
        <label>Tipo de propiedad
            <select name="tipo">
                <option value="">Cualquiera</option>
                <c:forEach var="t" items="${tiposPropiedad}">
                    <option value="${t}" ${criterio.tipo == t ? 'selected' : ''}><c:out value="${t.etiqueta}"/></option>
                </c:forEach>
            </select>
        </label>
        <label>Distrito
            <input type="text" name="distrito" maxlength="60" value="<c:out value='${criterio.distrito}'/>" placeholder="Ej. Cayma">
        </label>
        <label>Precio máximo (S/)
            <input type="number" name="precioMax" min="0" step="0.01" value="<c:out value='${criterio.precioMaximo}'/>">
            <c:if test="${not empty errores.precioMax}"><small class="error"><c:out value="${errores.precioMax}"/></small></c:if>
        </label>
        <label>Metraje mínimo (m²)
            <input type="number" name="metrajeMin" min="0" step="0.01" value="<c:out value='${criterio.metrajeMinimo}'/>">
        </label>
        <label>Habitaciones mínimas
            <input type="number" name="habitaciones" min="0" max="50" value="<c:out value='${criterio.habitacionesMinimas}'/>">
        </label>
    </div>
    <button type="submit" name="buscar" value="1" class="boton">Buscar</button>
</form>

<h2><c:out value="${busco ? 'Resultados de la búsqueda' : 'Cartera reciente'}"/> <small>(${inmuebles.size()})</small></h2>

<c:choose>
    <c:when test="${empty inmuebles}">
        <div class="alerta aviso">
            No encontramos inmuebles con esas características.
            <a class="boton" href="${pageContext.request.contextPath}/pedido?operacion=${criterio.operacion}&amp;tipo=${criterio.tipo}&amp;distrito=<c:out value='${criterio.distrito}'/>&amp;precioMax=${criterio.precioMaximo}&amp;metrajeMin=${criterio.metrajeMinimo}&amp;habitaciones=${criterio.habitacionesMinimas}">Registrar un pedido</a>
        </div>
    </c:when>
    <c:otherwise>
        <div class="cartera">
            <c:forEach var="i" items="${inmuebles}">
                <article class="tarjeta inmueble">
                    <span class="etiqueta ${i.operacionOfrecida == 'VENTA' ? 'venta' : 'alquiler'}"><c:out value="${i.operacionOfrecida.etiqueta}"/></span>
                    <h3><c:out value="${i.tipo.etiqueta}"/> en <c:out value="${i.distrito}"/></h3>
                    <p class="precio">S/ <fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/>
                        <c:if test="${i.operacionOfrecida == 'ALQUILER'}"><small>/ mes</small></c:if></p>
                    <p><c:out value="${i.metraje}"/> m²
                        <c:if test="${i.habitaciones > 0}"> · <c:out value="${i.habitaciones}"/> hab.</c:if>
                        <c:if test="${i.cochera}"> · cochera</c:if></p>
                    <p class="tenue"><c:out value="${i.referencias}"/></p>
                </article>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>
<%@ include file="comun/pie.jspf" %>
