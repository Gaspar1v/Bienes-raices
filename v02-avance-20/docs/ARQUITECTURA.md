# Arquitectura del Portal Inmobiliaria Bienes Raíces S.A.C.

Documento para desarrolladores. Complementa el Javadoc (`mvn javadoc:javadoc`, salida en `target/reports/apidocs/index.html`).

## Estilo arquitectónico

Aplicación web Java (Jakarta EE 10) organizada en capas con los patrones **MVC** y **DAO**:

```
Navegador ──► Filtros ──► Controlador (Servlet) ──► Servicio ──► DAO ──► MySQL 8
                              │
                              └──► Vista (JSP)
```

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Filtros | `pe.bienesraices.filtro` | Cabeceras de seguridad, MDC de Logback, CSRF, autenticación y rol |
| Controlador | `pe.bienesraices.controlador` | Recibe la petición, lee el formulario, llama al servicio y elige la vista |
| Vista | `src/main/webapp/WEB-INF/vistas` | Páginas JSP con JSTL; nunca se abren directamente |
| Servicio | `pe.bienesraices.servicio` | Reglas de negocio, validaciones y auditoría |
| DAO | `pe.bienesraices.dao`, `dao.impl` | Acceso a datos con JDBC y `PreparedStatement` |
| Modelo | `pe.bienesraices.modelo` | Entidades y enumeraciones del dominio |
| Configuración | `pe.bienesraices.config` | Arranque, conexión y contenedor de servicios (`Servicios`) |

`Servicios` crea una sola vez los servicios con sus DAO y los guarda en el `ServletContext`; cada servlet los obtiene con `servicios()` desde `BaseServlet`. Así los controladores dependen de los servicios y no de la base de datos (inversión de dependencias).

## Rutas

| Ruta | Servlet | Acceso |
|---|---|---|
| `/login`, `/logout` | `LoginServlet`, `LogoutServlet` | Público |
| `/app/panel` | `PanelServlet` | Todo el personal |

Las rutas `/app/*` se habilitan desde `Permisos`: cada `ModuloSistema` indica su ruta, los roles autorizados y si está activo. Un módulo inactivo aparece en el dashboard como **Próximamente** y su ruta queda denegada.

## Flujo del inicio de sesión

1. `GET /login` muestra el formulario con un token CSRF.
2. `POST /login` → `CsrfFiltro` valida el token → `LoginServlet` llama a `AuthService.autenticar`.
3. `AuthService` busca al usuario (`UsuarioDAO`), verifica bloqueo y contraseña (`PasswordHasher`) y registra el resultado en la bitácora (`Auditoria`).
4. Si es correcto, se cambia el ID de sesión, se guarda `UsuarioSesion` y se redirige a `/app/panel`.
5. `AutenticacionFiltro` verifica la sesión y el rol con `Permisos` antes de atender cualquier ruta `/app/*`.

## Seguridad

- Toda petición pasa por `CabecerasSeguridadFiltro` (CSP, `X-Frame-Options`, `nosniff`).
- Las rutas `/app/*` exigen sesión y rol (denegar por defecto).
- Todo `POST` exige el token CSRF de la sesión.
- Las vistas están en `WEB-INF` y no se pueden abrir directamente.
- Las credenciales de la base de datos se leen de variables de entorno (`BR_DB_URL`, `BR_DB_USER`, `BR_DB_PASSWORD`).
- `Auditoria` registra en `logs/auditoria.log` quién hizo cada acción y desde qué IP (MDC de Logback).

## Pruebas

`mvn test` ejecuta las pruebas JUnit 5 sobre una base H2 en modo MySQL creada con los mismos scripts del proyecto (`BaseDatosPrueba`).
