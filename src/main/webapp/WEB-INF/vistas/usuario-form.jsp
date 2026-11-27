<%@ include file="comun/cabecera.jspf" %>
<c:set var="e" value="${edicion}"/>
<c:set var="nuevo" value="${e.idUsuario == 0}"/>
<h1><c:out value="${nuevo ? 'Nuevo usuario' : 'Editar usuario'}"/></h1>
<c:if test="${not empty errores}">
    <div class="alerta peligro" role="alert">
        <c:forEach var="er" items="${errores}"><div><c:out value="${er.value}"/></div></c:forEach>
    </div>
</c:if>

<div class="dos-columnas">
    <form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/app/usuarios" novalidate>
        <input type="hidden" name="_csrf" value="${csrf}">
        <input type="hidden" name="id" value="${e.idUsuario}">
        <input type="hidden" name="accion" value="${nuevo ? 'crear' : 'actualizar'}">
        <div class="rejilla">
            <label>Usuario *
                <c:choose>
                    <c:when test="${nuevo}"><input type="text" name="username" maxlength="30" required value="<c:out value='${e.username}'/>"></c:when>
                    <c:otherwise><input type="text" value="<c:out value='${e.username}'/>" disabled></c:otherwise>
                </c:choose>
            </label>
            <label class="doble">Nombres y apellidos *
                <input type="text" name="nombres" maxlength="100" required value="<c:out value='${e.nombres}'/>">
            </label>
            <label>Rol *
                <select name="rol" required>
                    <option value="">Seleccione</option>
                    <c:forEach var="r" items="${roles}">
                        <option value="${r}" ${e.rol == r ? 'selected' : ''}><c:out value="${r.etiqueta}"/></option>
                    </c:forEach>
                </select>
            </label>
            <c:choose>
                <c:when test="${nuevo}">
                    <label>Contraseña inicial *
                        <input type="password" name="clave" maxlength="100" required autocomplete="new-password">
                        <small class="tenue">Mínimo 8 caracteres, con letras y números.</small>
                    </label>
                </c:when>
                <c:otherwise>
                    <label class="casilla"><input type="checkbox" name="activo" ${e.activo ? 'checked' : ''}> Cuenta activa</label>
                </c:otherwise>
            </c:choose>
        </div>
        <button type="submit" class="boton">Guardar</button>
        <a href="${pageContext.request.contextPath}/app/usuarios">Cancelar</a>
    </form>

    <c:if test="${not nuevo}">
        <form class="tarjeta formulario" method="post" action="${pageContext.request.contextPath}/app/usuarios" novalidate>
            <input type="hidden" name="_csrf" value="${csrf}">
            <input type="hidden" name="id" value="${e.idUsuario}">
            <input type="hidden" name="accion" value="clave">
            <h2>Restablecer contraseña</h2>
            <label>Nueva contraseña
                <input type="password" name="clave" maxlength="100" required autocomplete="new-password">
                <small class="tenue">Mínimo 8 caracteres, con letras y números. Se desbloquea la cuenta.</small>
            </label>
            <button type="submit" class="boton-borde">Restablecer</button>
        </form>
    </c:if>
</div>
<%@ include file="comun/pie.jspf" %>
