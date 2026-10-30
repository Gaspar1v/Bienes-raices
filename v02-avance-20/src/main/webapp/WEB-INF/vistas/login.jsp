<%@ include file="comun/cabecera.jspf" %>
<section class="tarjeta acceso">
    <h1>Ingreso del personal</h1>
    <c:if test="${not empty error}"><div class="alerta peligro" role="alert"><c:out value="${error}"/></div></c:if>
    <form method="post" action="${pageContext.request.contextPath}/login" autocomplete="off">
        <input type="hidden" name="_csrf" value="${csrf}">
        <label>Usuario
            <input type="text" name="username" maxlength="50" required autofocus value="<c:out value='${username}'/>">
        </label>
        <label>Contraseña
            <input type="password" name="password" maxlength="100" required autocomplete="current-password">
        </label>
        <button type="submit" class="boton ancho">Ingresar</button>
    </form>
    <p class="tenue">Por seguridad, la cuenta se bloquea 15 minutos después de 5 intentos fallidos.</p>
</section>
<%@ include file="comun/pie.jspf" %>
