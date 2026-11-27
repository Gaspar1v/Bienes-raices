<%@ include file="comun/cabecera.jspf" %>
<div class="titulo-accion">
    <h1>Usuarios y agentes</h1>
    <c:if test="${puedeAdministrar}">
        <a class="boton" href="${pageContext.request.contextPath}/app/usuarios?accion=nuevo">+ Nuevo usuario</a>
    </c:if>
</div>

<h2>Desempeño de los agentes inmobiliarios</h2>
<table class="tabla">
    <thead><tr><th>Agente</th><th>Usuario</th><th>Operaciones</th><th>Ventas</th><th>Alquileres</th><th>Comisión total (S/)</th><th>Estado</th></tr></thead>
    <tbody>
    <c:forEach var="a" items="${agentes}">
        <tr>
            <td><c:out value="${a.nombres}"/></td>
            <td><c:out value="${a.username}"/></td>
            <td class="num">${a.operaciones}</td>
            <td class="num">${a.ventas}</td>
            <td class="num">${a.alquileres}</td>
            <td class="num"><fmt:formatNumber value="${a.comisionTotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
            <td><span class="estado ${a.activo ? 'NUEVO_INGRESO' : 'INACTIVO'}">${a.activo ? 'Activo' : 'Inactivo'}</span></td>
        </tr>
    </c:forEach>
    <c:if test="${empty agentes}"><tr><td colspan="7" class="tenue">No hay agentes registrados.</td></tr></c:if>
    </tbody>
</table>

<h2>Usuarios del sistema</h2>
<table class="tabla">
    <thead><tr><th>Nombres</th><th>Usuario</th><th>Rol</th><th>Estado</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="u" items="${usuarios}">
        <tr>
            <td><c:out value="${u.nombres}"/></td>
            <td><c:out value="${u.username}"/></td>
            <td><c:out value="${u.rol.etiqueta}"/></td>
            <td><span class="estado ${u.activo ? 'NUEVO_INGRESO' : 'INACTIVO'}">${u.activo ? 'Activo' : 'Inactivo'}</span></td>
            <td>
                <c:if test="${puedeAdministrar}">
                    <a href="${pageContext.request.contextPath}/app/usuarios?accion=editar&amp;id=${u.idUsuario}">Editar</a>
                </c:if>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<%@ include file="comun/pie.jspf" %>
