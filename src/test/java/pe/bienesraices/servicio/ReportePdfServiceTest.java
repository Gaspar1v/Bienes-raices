package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.junit.jupiter.api.Test;

import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

class ReportePdfServiceTest {
    private final ReportePdfService servicio = new ReportePdfService();

    private static Operacion operacion(int id, TipoOperacion tipo, String fecha, String monto, String cliente) {
        Operacion o = new Operacion();
        o.setIdOperacion(id);
        o.setIdInmueble(id);
        o.setTipo(tipo);
        o.setDescripcionInmueble("#" + id + " CASA - Cayma");
        o.setNombrePropietario("Jorge Salas Valdivia");
        o.setClienteNombre(cliente);
        o.setClienteDni("72345678");
        o.setNombreAgente("Carlos Huaman Flores");
        o.setFechaInicio(LocalDate.parse(fecha));
        if (tipo == TipoOperacion.ALQUILER) {
            o.setFechaFin(LocalDate.parse(fecha).plusYears(1));
        }
        o.setMonto(new BigDecimal(monto));
        o.setComision(tipo.calcularComision(new BigDecimal(monto)));
        return o;
    }

    private static String texto(byte[] pdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            return new PDFTextStripper().getText(doc);
        }
    }

    private static int paginas(byte[] pdf) throws IOException {
        try (PDDocument doc = Loader.loadPDF(pdf)) {
            return doc.getNumberOfPages();
        }
    }

    @Test
    void elReporteDeOperacionesIncluyeTotalesYConsolidado() throws IOException {
        byte[] pdf = servicio.reporteOperaciones(List.of(
                operacion(1, TipoOperacion.VENTA, "2026-09-05", "395000", "Andrea Quispe Mamani"),
                operacion(2, TipoOperacion.ALQUILER, "2026-10-01", "1400", "Luis Apaza Condori"),
                operacion(3, TipoOperacion.VENTA, "2026-10-15", "480000", "Rocío Peña Linares")),
                LocalDate.parse("2026-09-01"), LocalDate.parse("2026-10-31"));
        String texto = texto(pdf);
        assertTrue(texto.contains("Reporte de ventas y alquileres"));
        assertTrue(texto.contains("TOTAL"));
        assertTrue(texto.contains("S/ 876,400.00"));
        assertTrue(texto.contains("S/ 27,650.00"));
        assertTrue(texto.contains("Consolidado mensual"));
        assertTrue(texto.contains("Rocío Peña"));
        assertTrue(texto.contains("Página 1 de 1"));
    }

    @Test
    void lasTablasLargasContinuanEnLaSiguientePagina() throws IOException {
        List<Operacion> muchas = new ArrayList<>();
        for (int i = 1; i <= 80; i++) {
            muchas.add(operacion(i, TipoOperacion.ALQUILER, "2026-10-01", "1500", "Cliente " + i));
        }
        byte[] pdf = servicio.reporteOperaciones(muchas, LocalDate.parse("2026-10-01"), LocalDate.parse("2026-10-31"));
        int total = paginas(pdf);
        assertTrue(total > 1);
        assertTrue(texto(pdf).contains("Página " + total + " de " + total));
    }

    @Test
    void elContratoDeVentaIncluyeLasPartesYElPrecio() throws IOException {
        Inmueble in = new Inmueble();
        in.setIdInmueble(1);
        in.setTipo(TipoPropiedad.CASA);
        in.setOperacionOfrecida(TipoOperacion.VENTA);
        in.setMetraje(new BigDecimal("180.00"));
        in.setDistrito("Cayma");
        in.setDireccion("Av. Cayma 512");
        in.setHabitaciones(4);
        in.setCochera(true);
        in.setEstado(EstadoInmueble.VENDIDO);
        String texto = texto(servicio.contrato(operacion(5, TipoOperacion.VENTA, "2026-10-20", "480000", "Rocío Peña Linares"), in));
        assertTrue(texto.contains("CONTRATO DE COMPRAVENTA DE INMUEBLE"));
        assertTrue(texto.contains("Rocío Peña Linares"));
        assertTrue(texto.contains("S/ 480,000.00"));
        assertTrue(texto.contains("S/ 14,400.00"));
    }

    @Test
    void elContratoDeAlquilerIndicaElPlazo() throws IOException {
        Inmueble in = new Inmueble();
        in.setTipo(TipoPropiedad.DEPARTAMENTO);
        in.setMetraje(new BigDecimal("70.00"));
        in.setDistrito("Cayma");
        in.setDireccion("Av. Ejército 890 Dpto 201");
        String texto = texto(servicio.contrato(operacion(2, TipoOperacion.ALQUILER, "2026-10-01", "1400", "Luis Apaza Condori"), in));
        assertTrue(texto.contains("CONTRATO DE ARRENDAMIENTO DE INMUEBLE"));
        assertTrue(texto.contains("01/10/2027"));
    }
}
