package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;

class ReporteExcelServiceTest {
    private final ReporteExcelService servicio = new ReporteExcelService();

    private static Operacion operacion(int id, TipoOperacion tipo, String fecha, String monto, String cliente) {
        Operacion o = new Operacion();
        o.setIdOperacion(id);
        o.setTipo(tipo);
        o.setDescripcionInmueble("#" + id + " CASA - Cayma");
        o.setNombrePropietario("Jorge Salas");
        o.setClienteNombre(cliente);
        o.setNombreAgente("Carlos Huaman");
        o.setFechaInicio(LocalDate.parse(fecha));
        o.setMonto(new BigDecimal(monto));
        o.setComision(tipo.calcularComision(new BigDecimal(monto)));
        return o;
    }

    @Test
    void generaDetalleConTotalesYConsolidadoMensual() throws Exception {
        List<Operacion> ops = List.of(
                operacion(1, TipoOperacion.VENTA, "2026-09-05", "395000", "Andrea Quispe"),
                operacion(2, TipoOperacion.ALQUILER, "2026-10-01", "1400", "Luis Apaza"),
                operacion(3, TipoOperacion.VENTA, "2026-10-15", "200000", "Rocio Paz"));

        byte[] excel = servicio.reporteOperaciones(ops, LocalDate.parse("2026-09-01"), LocalDate.parse("2026-10-31"));

        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            Sheet detalle = wb.getSheet("Operaciones");
            Row totales = detalle.getRow(6);
            assertEquals(CellType.FORMULA, totales.getCell(9).getCellType());
            assertEquals("SUM(J4:J6)", totales.getCell(9).getCellFormula());

            Sheet consolidado = wb.getSheet("Consolidado mensual");
            assertEquals(3, consolidado.getLastRowNum() - 2);
            Row ventasOctubre = consolidado.getRow(5);
            assertEquals("2026-10", ventasOctubre.getCell(0).getStringCellValue());
            assertEquals(6000.0, ventasOctubre.getCell(4).getNumericCellValue(), 0.001);
        }
    }

    @Test
    void neutralizaInyeccionDeFormulas() {
        assertEquals("'=HYPERLINK(\"http://malicioso\")", ReporteExcelService.textoSeguro("=HYPERLINK(\"http://malicioso\")"));
        assertEquals("'+51 999", ReporteExcelService.textoSeguro("+51 999"));
        assertEquals("Cayma", ReporteExcelService.textoSeguro("Cayma"));
    }

    @Test
    void textoMaliciosoQuedaComoTextoEnLaCelda() throws Exception {
        byte[] excel = servicio.reporteOperaciones(
                List.of(operacion(1, TipoOperacion.VENTA, "2026-10-01", "100000", "=1+1")),
                LocalDate.parse("2026-10-01"), LocalDate.parse("2026-10-31"));
        try (XSSFWorkbook wb = new XSSFWorkbook(new ByteArrayInputStream(excel))) {
            var celda = wb.getSheet("Operaciones").getRow(3).getCell(4);
            assertEquals(CellType.STRING, celda.getCellType());
            assertTrue(celda.getStringCellValue().startsWith("'"));
        }
    }
}
