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
| `/app/inmuebles` | `InmuebleServlet` | Personal; editan asistente y administrador |
| `/inicio`, `/pedido` | `PortalServlet`, `PedidoServlet` | Público (búsqueda y pedidos) |
| `/app/pedidos` | `PedidosInternosServlet` | Agente, gerente y administrador |
| `/app/operaciones` | `OperacionServlet` | Agente, asistente y administrador |
| `/app/reportes` | `ReporteServlet` | Gerente y administrador |
| `/captacion` | `CaptacionPublicaServlet` | Público (propietarios) |
| `/app/captacion` | `CaptacionServlet` | Agente, asistente y administrador |

Las rutas `/app/*` se habilitan desde `Permisos`: cada `ModuloSistema` indica su ruta, los roles autorizados y si está activo. Un módulo inactivo aparece en el dashboard como **Próximamente** y su ruta queda denegada.

## Flujo del inicio de sesión

1. `GET /login` muestra el formulario con un token CSRF.
2. `POST /login` → `CsrfFiltro` valida el token → `LoginServlet` llama a `AuthService.autenticar`.
3. `AuthService` busca al usuario (`UsuarioDAO`), verifica bloqueo y contraseña (`PasswordHasher`) y registra el resultado en la bitácora (`Auditoria`).
4. Si es correcto, se cambia el ID de sesión, se guarda `UsuarioSesion` y se redirige a `/app/panel`.
5. `AutenticacionFiltro` verifica la sesión y el rol con `Permisos` antes de atender cualquier ruta `/app/*`.

## Flujo de inmuebles

`InmuebleServlet` lista con filtros (`CriterioBusqueda`), muestra el detalle con su historial y, para asistente y administrador, registra o modifica. `InmuebleService` valida los datos con `Validador` y lanza `ValidacionException` con los errores por campo; `InmuebleDAOImpl` guarda el inmueble y el registro en `historial_estado` dentro de una transacción.

## Flujo de búsqueda y pedidos

`PortalServlet` (`/inicio`) muestra solo inmuebles disponibles y aplica los filtros de tipo, operación, distrito y precio. Si el cliente no encuentra lo que busca registra un pedido en `/pedido`; el personal lo consulta en `/app/pedidos`.

## Flujo de operaciones

`OperacionService.registrar` valida al cliente y las fechas; `OperacionDAOImpl` bloquea el inmueble con `SELECT … FOR UPDATE`, comprueba que siga disponible, inserta la operación con su comisión (3 % en ventas, un mes de renta en alquileres) y cambia el estado del inmueble a vendido o alquilado, todo en una sola transacción.

## Reportes

`ReporteExcelService` arma los libros con Apache POI (`XSSFWorkbook`, estilos, fórmulas de total y protección contra la inyección de fórmulas). `ZipSeguro` empaqueta varios reportes y valida las rutas para evitar *Zip Slip*.
`ReportePdfService` genera con Apache PDFBox el reporte de operaciones con totales y consolidado mensual, la cartera de inmuebles, los listados de clientes y empleados y los contratos de compraventa o arrendamiento. `DocumentoPdf` encapsula la paginación, las tablas con encabezado repetido y el pie «Página x de n».

## Captación de inmuebles (RF005)

1. El propietario completa el formulario público `/captacion`; `CaptacionService.registrar` valida los datos y guarda la solicitud con estado `PENDIENTE`.
2. El personal revisa las solicitudes en `/app/captacion` y puede **observarlas** (con un motivo) o **aceptarlas**.
3. Al aceptar, `SolicitudCaptacionDAOImpl.aceptar` bloquea la solicitud, reutiliza o crea al propietario según su DNI, registra el inmueble en la cartera y marca la solicitud como `ACEPTADA`, todo en una transacción.

## Seguridad

- Toda petición pasa por `CabecerasSeguridadFiltro` (CSP, `X-Frame-Options`, `nosniff`).
- Las rutas `/app/*` exigen sesión y rol (denegar por defecto).
- Todo `POST` exige el token CSRF de la sesión.
- Las vistas están en `WEB-INF` y no se pueden abrir directamente.
- Las credenciales de la base de datos se leen de variables de entorno (`BR_DB_URL`, `BR_DB_USER`, `BR_DB_PASSWORD`).
- `Auditoria` registra en `logs/auditoria.log` quién hizo cada acción y desde qué IP (MDC de Logback).

## Pruebas

`mvn test` ejecuta las pruebas JUnit 5 sobre una base H2 en modo MySQL creada con los mismos scripts del proyecto (`BaseDatosPrueba`).
