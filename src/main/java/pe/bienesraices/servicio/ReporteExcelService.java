package pe.bienesraices.servicio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;

public class ReporteExcelService {
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("yyyy-MM");

    private static final class Estilos {
        final CellStyle encabezado;
        final CellStyle moneda;
        final CellStyle fecha;
        final CellStyle total;
        final CellStyle titulo;

        Estilos(Workbook wb) {
            Font negrita = wb.createFont();
            negrita.setBold(true);
            negrita.setColor(IndexedColors.WHITE.getIndex());

            encabezado = wb.createCellStyle();
            encabezado.setFont(negrita);
            encabezado.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex());
            encabezado.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            encabezado.setBorderBottom(BorderStyle.THIN);

            moneda = wb.createCellStyle();
            moneda.setDataFormat(wb.createDataFormat().getFormat("\"S/\" #,##0.00"));

            fecha = wb.createCellStyle();
            fecha.setDataFormat(wb.createDataFormat().getFormat("dd/mm/yyyy"));

            Font fuenteTotal = wb.createFont();
            fuenteTotal.setBold(true);
            total = wb.createCellStyle();
            total.cloneStyleFrom(moneda);
            total.setFont(fuenteTotal);
            total.setBorderTop(BorderStyle.DOUBLE);

            Font fuenteTitulo = wb.createFont();
            fuenteTitulo.setBold(true);
            fuenteTitulo.setFontHeightInPoints((short) 14);
            titulo = wb.createCellStyle();
            titulo.setFont(fuenteTitulo);
        }
    }

    public byte[] reporteInmuebles(List<Inmueble> inmuebles) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            propiedades(wb, "Cartera de inmuebles");
            Estilos e = new Estilos(wb);
            Sheet hoja = wb.createSheet("Inmuebles");
            String[] columnas = {"ID", "Tipo", "Operacion", "Distrito", "Direccion", "Metraje (m2)",
                    "Precio", "Habitaciones", "Cochera", "Propietario", "Estado"};
            int fila = titulo(hoja, e, "Cartera de inmuebles - Bienes Raices S.A.C.", columnas.length);
            encabezados(hoja, e, fila++, columnas);
            int primera = fila;
            for (Inmueble in : inmuebles) {
                Row r = hoja.createRow(fila++);
                r.createCell(0).setCellValue(in.getIdInmueble());
                r.createCell(1).setCellValue(in.getTipo().getEtiqueta());
                r.createCell(2).setCellValue(in.getOperacionOfrecida().getEtiqueta());
                r.createCell(3).setCellValue(textoSeguro(in.getDistrito()));
                r.createCell(4).setCellValue(textoSeguro(in.getDireccion()));
                r.createCell(5).setCellValue(in.getMetraje().doubleValue());
                celdaMoneda(r, 6, in.getPrecio(), e);
                r.createCell(7).setCellValue(in.getHabitaciones() == null ? 0 : in.getHabitaciones());
                r.createCell(8).setCellValue(in.isCochera() ? "Si" : "No");
                r.createCell(9).setCellValue(textoSeguro(in.getNombrePropietario()));
                r.createCell(10).setCellValue(in.getEstado().getEtiqueta());
            }
            finalizar(hoja, primera - 1, fila - 1, columnas.length);
            return escribir(wb);
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo generar el reporte de inmuebles", ex);
        }
    }

    public byte[] reporteOperaciones(List<Operacion> operaciones, LocalDate desde, LocalDate hasta) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            propiedades(wb, "Operaciones " + desde + " a " + hasta);
            Estilos e = new Estilos(wb);
            hojaDetalle(wb.createSheet("Operaciones"), e, operaciones, desde, hasta);
            hojaConsolidado(wb.createSheet("Consolidado mensual"), e, operaciones);
            return escribir(wb);
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo generar el reporte de operaciones", ex);
        }
    }

    private void hojaDetalle(Sheet hoja, Estilos e, List<Operacion> operaciones, LocalDate desde, LocalDate hasta) {
        String[] columnas = {"ID", "Tipo", "Inmueble", "Propietario", "Cliente", "Agente",
                "Fecha inicio", "Fecha fin", "Monto", "Comision"};
        int fila = titulo(hoja, e, "Reporte de ventas y alquileres del " + desde + " al " + hasta, columnas.length);
        encabezados(hoja, e, fila++, columnas);
        int primera = fila;
        for (Operacion o : operaciones) {
            Row r = hoja.createRow(fila++);
            r.createCell(0).setCellValue(o.getIdOperacion());
            r.createCell(1).setCellValue(o.getTipo().getEtiqueta());
            r.createCell(2).setCellValue(textoSeguro(o.getDescripcionInmueble()));
            r.createCell(3).setCellValue(textoSeguro(o.getNombrePropietario()));
            r.createCell(4).setCellValue(textoSeguro(o.getClienteNombre()));
            r.createCell(5).setCellValue(textoSeguro(o.getNombreAgente()));
            celdaFecha(r, 6, o.getFechaInicio(), e);
            celdaFecha(r, 7, o.getFechaFin(), e);
            celdaMoneda(r, 8, o.getMonto(), e);
            celdaMoneda(r, 9, o.getComision(), e);
        }
        int ultima = fila - 1;
        if (!operaciones.isEmpty()) {
            Row totales = hoja.createRow(fila);
            totales.createCell(7).setCellValue("TOTAL");
            for (int col : new int[] {8, 9}) {
                Cell c = totales.createCell(col);
                String letra = org.apache.poi.ss.util.CellReference.convertNumToColString(col);
                c.setCellFormula("SUM(" + letra + (primera + 1) + ":" + letra + (ultima + 1) + ")");
                c.setCellStyle(e.total);
            }
        }
        finalizar(hoja, primera - 1, ultima, columnas.length);
    }

    private void hojaConsolidado(Sheet hoja, Estilos e, List<Operacion> operaciones) {
        Map<String, BigDecimal[]> consolidado = new TreeMap<>();
        for (Operacion o : operaciones) {
            String clave = YearMonth.from(o.getFechaInicio()).format(MES) + "|" + o.getTipo().name();
            BigDecimal[] acc = consolidado.computeIfAbsent(clave,
                    k -> new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
            acc[0] = acc[0].add(BigDecimal.ONE);
            acc[1] = acc[1].add(o.getMonto());
            acc[2] = acc[2].add(o.getComision());
        }
        String[] columnas = {"Mes", "Tipo", "Cantidad", "Monto total", "Comisión total"};
        int fila = titulo(hoja, e, "Consolidado mensual de operaciones y comisiones", columnas.length);
        encabezados(hoja, e, fila++, columnas);
        int primera = fila;
        for (Map.Entry<String, BigDecimal[]> entrada : consolidado.entrySet()) {
            String[] partes = StringUtils.split(entrada.getKey(), '|');
            Row r = hoja.createRow(fila++);
            r.createCell(0).setCellValue(partes[0]);
            r.createCell(1).setCellValue(TipoOperacion.valueOf(partes[1]).getEtiqueta());
            r.createCell(2).setCellValue(entrada.getValue()[0].intValue());
            celdaMoneda(r, 3, entrada.getValue()[1], e);
            celdaMoneda(r, 4, entrada.getValue()[2], e);
        }
        finalizar(hoja, primera - 1, fila - 1, columnas.length);
    }

    public static String textoSeguro(String valor) {
        if (StringUtils.isEmpty(valor)) {
            return "";
        }
        return StringUtils.startsWithAny(valor, "=", "+", "-", "@", "\t", "\r") ? "'" + valor : valor;
    }

    private static void propiedades(XSSFWorkbook wb, String asunto) {
        wb.getProperties().getCoreProperties().setCreator("Portal Bienes Raices S.A.C.");
        wb.getProperties().getCoreProperties().setTitle(asunto);
    }

    private static int titulo(Sheet hoja, Estilos e, String texto, int columnas) {
        Cell c = hoja.createRow(0).createCell(0);
        c.setCellValue(texto);
        c.setCellStyle(e.titulo);
        hoja.addMergedRegion(new CellRangeAddress(0, 0, 0, columnas - 1));
        return 2;
    }

    private static void encabezados(Sheet hoja, Estilos e, int fila, String[] columnas) {
        Row r = hoja.createRow(fila);
        for (int i = 0; i < columnas.length; i++) {
            Cell c = r.createCell(i);
            c.setCellValue(columnas[i]);
            c.setCellStyle(e.encabezado);
        }
    }

    private static void finalizar(Sheet hoja, int filaEncabezado, int ultimaFila, int columnas) {
        hoja.createFreezePane(0, filaEncabezado + 1);
        hoja.setAutoFilter(new CellRangeAddress(filaEncabezado, Math.max(filaEncabezado, ultimaFila), 0, columnas - 1));
        for (int i = 0; i < columnas; i++) {
            hoja.autoSizeColumn(i);

            int ancho = Math.max(hoja.getColumnWidth(i) + 4 * 256, 12 * 256);
            hoja.setColumnWidth(i, Math.min(ancho, 60 * 256));
        }
    }

    private static void celdaMoneda(Row r, int col, BigDecimal valor, Estilos e) {
        Cell c = r.createCell(col);
        c.setCellValue(valor == null ? 0 : valor.doubleValue());
        c.setCellStyle(e.moneda);
    }

    private static void celdaFecha(Row r, int col, LocalDate valor, Estilos e) {
        Cell c = r.createCell(col);
        if (valor != null) {
            c.setCellValue(valor);
            c.setCellStyle(e.fecha);
        }
    }

    private static byte[] escribir(Workbook wb) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        wb.write(out);
        return out.toByteArray();
    }
}
