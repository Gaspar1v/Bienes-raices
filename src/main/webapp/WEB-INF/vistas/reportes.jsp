<%@ include file="comun/cabecera.jspf" %>
<h1>Reportes gerenciales</h1>
<p>Los reportes se generan en Excel (.xlsx) con Apache POI y en PDF con Apache PDFBox.</p>
<c:if test="${not empty errores}">
    <div class="alerta peligro" role="alert">
        <c:forEach var="e" items="${errores}"><div><c:out value="${e.value}"/></div></c:forEach>
    </div>
</c:if>

<div class="dos-columnas">
    <section class="tarjeta">
        <h2>Ventas, alquileres y comisiones</h2>
        <p class="tenue">Detalle por rango de fechas, total de comisiones y consolidado mensual.</p>
        <form method="get" action="${pageContext.request.contextPath}/app/reportes" class="formulario">
            <div class="rejilla">
                <label>Desde <input type="date" name="desde" required value="<c:out value='${desde}'/>"></label>
                <label>Hasta <input type="date" name="hasta" required value="<c:out value='${hasta}'/>"></label>
            </div>
            <div class="acciones-reporte">
                <button type="submit" name="descargar" value="operaciones" class="boton">Descargar Excel</button>
                <button type="submit" name="descargar" value="operaciones_pdf" class="boton">Descargar PDF</button>
                <button type="submit" name="descargar" value="paquete" class="boton-borde">Paquete .zip (todos)</button>
            </div>
        </form>
    </section>
    <section class="tarjeta">
        <h2>Cartera de inmuebles</h2>
        <p class="tenue">Todos los inmuebles con su propietario y estado actual.</p>
        <div class="acciones-reporte">
            <a class="boton" href="${pageContext.request.contextPath}/app/reportes?descargar=inmuebles">Descargar Excel</a>
            <a class="boton" href="${pageContext.request.contextPath}/app/reportes?descargar=inmuebles_pdf">Descargar PDF</a>
        </div>
    </section>
</div>
<%@ include file="comun/pie.jspf" %>
