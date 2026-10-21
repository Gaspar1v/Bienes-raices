# Arquitectura del Portal Inmobiliaria Bienes Raíces S.A.C.

Documento para desarrolladores. Complementa el Javadoc (`mvn javadoc:javadoc`, salida en `target/reports/apidocs/index.html`).

## 1. Estilo arquitectónico

Aplicación web Java (Jakarta EE 10) organizada en capas con los patrones **MVC** y **DAO**:

```
Navegador ──► Filtros ──► Controlador (Servlet) ──► Servicio ──► DAO ──► MySQL 8
                              │
                              └──► Vista (JSP)
```

| Capa | Paquete | En esta entrega |
|---|---|---|
| Filtros | `pe.bienesraices.filtro` | Cabeceras de seguridad, MDC de Logback, CSRF, autenticación y rol |
| Controlador | `pe.bienesraices.controlador` | `LoginServlet`, `LogoutServlet`, `PanelServlet` |
| Vista | `src/main/webapp/WEB-INF/vistas` | `login.jsp`, `panel.jsp` (dashboard con pestañas), `error.jsp` |
| Servicio | `pe.bienesraices.servicio` | `AuthService` |
| DAO | `pe.bienesraices.dao`, `dao.impl` | `UsuarioDAO` / `UsuarioDAOImpl` con `PreparedStatement` |
| Modelo | `pe.bienesraices.modelo` | `Usuario`, `Rol`, `UsuarioSesion`, `ModuloSistema` |

## 2. Flujo del inicio de sesión

1. `GET /login` muestra el formulario con un token CSRF.
2. `POST /login` → `CsrfFiltro` valida el token → `LoginServlet` llama a `AuthService.autenticar`.
3. `AuthService` busca al usuario (`UsuarioDAO`), verifica bloqueo y contraseña (`PasswordHasher`), registra el resultado en la bitácora (`Auditoria`).
4. Si es correcto, se cambia el ID de sesión, se guarda `UsuarioSesion` y se redirige a `/app/panel`.
5. `AutenticacionFiltro` verifica la sesión y el rol con `Permisos` antes de mostrar el dashboard.

## 3. Dashboard

`PanelServlet` obtiene los módulos planificados de `Permisos.modulos()` (lista inmutable de `ModuloSistema`) y calcula a cuántos tendrá acceso el rol del usuario. La vista muestra la pestaña **Inicio** activa y las demás deshabilitadas con la etiqueta **Próximamente**; cada módulo se activará en los siguientes avances.

## 4. Seguridad

- Toda petición pasa por `CabecerasSeguridadFiltro` (CSP, `X-Frame-Options`, `nosniff`).
- Las rutas `/app/*` exigen sesión y rol (denegar por defecto).
- Todo `POST` exige el token CSRF de la sesión.
- Las vistas están en `WEB-INF` y no se pueden abrir directamente.
- Las credenciales de la base de datos se leen de variables de entorno (`BR_DB_URL`, `BR_DB_USER`, `BR_DB_PASSWORD`).

## 5. Pruebas

`mvn test` ejecuta las pruebas JUnit 5 sobre una base H2 en modo MySQL creada con los mismos scripts del proyecto.
