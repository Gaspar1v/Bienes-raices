package pe.bienesraices.config;

import java.time.Clock;

import jakarta.servlet.ServletContext;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.impl.InmuebleDAOImpl;
import pe.bienesraices.dao.impl.OperacionDAOImpl;
import pe.bienesraices.dao.impl.PedidoDAOImpl;
import pe.bienesraices.dao.impl.SolicitudCaptacionDAOImpl;
import pe.bienesraices.dao.impl.UsuarioDAOImpl;
import pe.bienesraices.servicio.AuthService;
import pe.bienesraices.servicio.CaptacionService;
import pe.bienesraices.servicio.InmuebleService;
import pe.bienesraices.servicio.OperacionService;
import pe.bienesraices.servicio.PedidoService;
import pe.bienesraices.servicio.ReporteExcelService;
import pe.bienesraices.servicio.ReportePdfService;

public final class Servicios {
    private static final String ATRIBUTO = Servicios.class.getName();

    private final AuthService auth;
    private final InmuebleService inmuebles;
    private final PedidoService pedidos;
    private final OperacionService operaciones;
    private final ReporteExcelService reportes;
    private final CaptacionService captacion;
    private final ReportePdfService reportesPdf;

    public Servicios(FabricaConexion conexiones) {
        this.auth = new AuthService(new UsuarioDAOImpl(conexiones), Clock.systemDefaultZone());
        this.inmuebles = new InmuebleService(new InmuebleDAOImpl(conexiones));
        this.pedidos = new PedidoService(new PedidoDAOImpl(conexiones));
        this.operaciones = new OperacionService(new OperacionDAOImpl(conexiones));
        this.reportes = new ReporteExcelService();
        this.captacion = new CaptacionService(new SolicitudCaptacionDAOImpl(conexiones));
        this.reportesPdf = new ReportePdfService();
    }

    public void registrarEn(ServletContext contexto) {
        contexto.setAttribute(ATRIBUTO, this);
    }

    public static Servicios de(ServletContext contexto) {
        return (Servicios) contexto.getAttribute(ATRIBUTO);
    }

    public AuthService auth() { return auth; }
    public InmuebleService inmuebles() { return inmuebles; }
    public PedidoService pedidos() { return pedidos; }
    public OperacionService operaciones() { return operaciones; }
    public ReporteExcelService reportes() { return reportes; }
    public CaptacionService captacion() { return captacion; }
    public ReportePdfService reportesPdf() { return reportesPdf; }
}
