<%@ include file="comun/cabecera.jspf" %>
<c:set var="u" value="${sessionScope.usuario}"/>
<section class="dashboard">
    <div class="dash-cabecera">
        <p class="tenue">Panel principal</p>
        <h1>Hola, <c:out value="${u.nombres}"/></h1>
        <p class="tenue">Rol: <strong><c:out value="${u.rol.etiqueta}"/></strong> · Sesión iniciada el <c:out value="${sessionScope.inicioSesion}"/></p>
    </div>

    <div class="pestanas" role="tablist" aria-label="Módulos del sistema">
        <button type="button" class="pestana activa" role="tab" aria-selected="true" aria-controls="tab-inicio" id="t-inicio">Inicio</button>
        <c:forEach var="m" items="${modulos}">
            <button type="button" class="pestana" role="tab" aria-selected="false" aria-disabled="true" disabled
                    title="Disponible en un próximo avance">
                <c:out value="${m.nombre}"/> <span class="insignia">Próximamente</span>
            </button>
        </c:forEach>
    </div>

    <div id="tab-inicio" role="tabpanel" aria-labelledby="t-inicio" class="panel-pestana">
        <div class="indicadores">
            <div class="tarjeta indicador"><span><c:out value="${u.rol.etiqueta}"/></span>tu rol en el sistema</div>
            <div class="tarjeta indicador"><span>${modulosConAcceso} de ${modulos.size()}</span>módulos a los que tendrás acceso</div>
            <div class="tarjeta indicador"><span>${minutosSesion} min</span>de inactividad cierran la sesión</div>
        </div>

        <div class="dos-columnas">
            <section class="tarjeta">
                <h2>Tu cuenta</h2>
                <dl class="ficha">
                    <dt>Nombres</dt><dd><c:out value="${u.nombres}"/></dd>
                    <dt>Usuario</dt><dd><c:out value="${u.username}"/></dd>
                    <dt>Rol</dt><dd><c:out value="${u.rol.etiqueta}"/></dd>
                    <dt>Inicio de sesión</dt><dd><c:out value="${sessionScope.inicioSesion}"/></dd>
                </dl>
            </section>
            <section class="tarjeta">
                <h2>Seguridad de tu acceso</h2>
                <ul class="lista-check">
                    <li>Contraseña guardada con hash PBKDF2 y sal; nadie puede leerla.</li>
                    <li>La cuenta se bloquea 15 minutos tras 5 intentos fallidos.</li>
                    <li>Cada formulario está protegido con un token anti-CSRF.</li>
                    <li>Solo ves las opciones permitidas para tu rol.</li>
                </ul>
            </section>
        </div>

        <section class="tarjeta">
            <h2>Avance del sistema</h2>
            <table class="tabla">
                <thead><tr><th>Módulo</th><th>Requisitos</th><th>Acceso con tu rol</th><th>Estado</th></tr></thead>
                <tbody>
                <tr>
                    <td>Inicio de sesión y panel</td><td>RF001</td><td>Sí</td>
                    <td><span class="estado NUEVO_INGRESO">Disponible</span></td>
                </tr>
                <c:forEach var="m" items="${modulos}">
                    <tr>
                        <td><strong><c:out value="${m.nombre}"/></strong><br><small class="tenue"><c:out value="${m.descripcion}"/></small></td>
                        <td><c:out value="${m.requisitos}"/></td>
                        <td>${m.permitidoPara(u.rol) ? 'Sí' : 'No'}</td>
                        <td><span class="estado EN_RESERVA">Próximamente</span></td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </section>
    </div>
</section>
<%@ include file="comun/pie.jspf" %>
