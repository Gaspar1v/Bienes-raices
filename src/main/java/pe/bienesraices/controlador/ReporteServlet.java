package pe.bienesraices.controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.servicio.ReporteExcelService;
import pe.bienesraices.servicio.ReportePdfService;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.ZipSeguro;

@WebServlet("/app/reportes")
public class ReporteServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private static final String PDF = "application/pdf";
    private static final long MAX_DIAS = 366 * 3;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String tipo = req.getParameter("descargar");
        if (tipo == null) {
            LocalDate hoy = LocalDate.now();
            req.setAttribute("desde", hoy.withDayOfYear(1));
            req.setAttribute("hasta", hoy);
            vista("reportes", req, res);
            return;
        }

        Formulario f = new Formulario(req);
        LocalDate desde = f.fecha("desde", "La fecha desde");
        LocalDate hasta = f.fecha("hasta", "La fecha hasta");
        boolean conRango = tipo.startsWith("operaciones") || tipo.equals("paquete");
        f.validador().requerido("desde", desde, "La fecha desde").requerido("hasta", hasta, "La fecha hasta");
        if (desde != null && hasta != null) {
            f.validador().regla(!hasta.isBefore(desde), "hasta", "La fecha hasta debe ser posterior a desde.")
                         .regla(ChronoUnit.DAYS.between(desde, hasta) <= MAX_DIAS, "hasta", "El rango máximo es de 3 años.");
        }
        if (conRango && !f.validador().esValido()) {
            req.setAttribute("errores", f.validador().getErrores());
            req.setAttribute("desde", desde);
            req.setAttribute("hasta", hasta);
            vista("reportes", req, res);
            return;
        }

        ReporteExcelService excel = servicios().reportes();
        ReportePdfService pdf = servicios().reportesPdf();
        CriterioBusqueda todos = new CriterioBusqueda();
        todos.setSoloDisponibles(false);
        switch (tipo) {
            case "inmuebles" -> {
                List<Inmueble> lista = servicios().inmuebles().buscar(todos);
                enviarArchivo(res, excel.reporteInmuebles(lista), XLSX, "cartera-inmuebles.xlsx");
            }
            case "inmuebles_pdf" -> enviarArchivo(res, pdf.reporteInmuebles(servicios().inmuebles().buscar(todos)), PDF, "cartera-inmuebles.pdf");
            case "operaciones" -> {
                List<Operacion> ops = servicios().operaciones().listarPorRango(desde, hasta);
                enviarArchivo(res, excel.reporteOperaciones(ops, desde, hasta), XLSX, "operaciones_" + desde + "_" + hasta + ".xlsx");
            }
            case "operaciones_pdf" -> {
                List<Operacion> ops = servicios().operaciones().listarPorRango(desde, hasta);
                enviarArchivo(res, pdf.reporteOperaciones(ops, desde, hasta), PDF, "operaciones_" + desde + "_" + hasta + ".pdf");
            }
            case "paquete" -> {
                Map<String, byte[]> archivos = new LinkedHashMap<>();
                List<Operacion> ops = servicios().operaciones().listarPorRango(desde, hasta);
                archivos.put("cartera-inmuebles.xlsx", excel.reporteInmuebles(servicios().inmuebles().buscar(todos)));
                archivos.put("operaciones.xlsx", excel.reporteOperaciones(ops, desde, hasta));
                archivos.put("operaciones.pdf", pdf.reporteOperaciones(ops, desde, hasta));
                enviarArchivo(res, ZipSeguro.comprimir(archivos), "application/zip", "reportes_" + desde + "_" + hasta + ".zip");
            }
            default -> {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
        }
        Auditoria.registrar("REPORTE_DESCARGADO", "tipo=" + tipo + (conRango ? " desde=" + desde + " hasta=" + hasta : ""));
    }
}
