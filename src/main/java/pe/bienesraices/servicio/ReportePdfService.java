package pe.bienesraices.servicio;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;

public class ReportePdfService {
    private static final String EMPRESA = "Inmobiliaria Bienes Raíces S.A.C.";
    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter MES = DateTimeFormatter.ofPattern("yyyy-MM");

    public byte[] reporteOperaciones(List<Operacion> operaciones, LocalDate desde, LocalDate hasta) {
        DocumentoPdf pdf = nuevo(true);
        pdf.titulo("Reporte de ventas y alquileres");
        pdf.subtitulo("Periodo: " + FECHA.format(desde) + " al " + FECHA.format(hasta) + " · Generado el " + FECHA_HORA.format(LocalDateTime.now()));

        pdf.seccion("Detalle de operaciones");
        List<String[]> filas = new ArrayList<>();
        BigDecimal totalMonto = BigDecimal.ZERO;
        BigDecimal totalComision = BigDecimal.ZERO;
        for (Operacion o : operaciones) {
            filas.add(new String[] {String.valueOf(o.getIdOperacion()), o.getTipo().getEtiqueta(), o.getDescripcionInmueble(),
                    o.getNombrePropietario(), o.getClienteNombre(), o.getNombreAgente(), fecha(o.getFechaInicio()),
                    fecha(o.getFechaFin()), moneda(o.getMonto()), moneda(o.getComision())});
            totalMonto = totalMonto.add(o.getMonto());
            totalComision = totalComision.add(o.getComision());
        }
        if (filas.isEmpty()) {
            pdf.parrafo("No se registraron operaciones en el periodo.");
        } else {
            filas.add(new String[] {"", "", "", "", "", "", "", "TOTAL", moneda(totalMonto), moneda(totalComision)});
            pdf.tabla(new String[] {"N.°", "Tipo", "Inmueble", "Propietario", "Cliente", "Agente", "Inicio", "Fin", "Monto", "Comisión"},
                    new float[] {4, 7, 14, 13, 13, 12, 8.5f, 8.5f, 11, 10}, filas, Set.of(0, 8, 9), true);
        }

        pdf.seccion("Consolidado mensual de operaciones y comisiones");
        Map<String, BigDecimal[]> consolidado = new TreeMap<>();
        for (Operacion o : operaciones) {
            String clave = YearMonth.from(o.getFechaInicio()).format(MES) + "|" + o.getTipo().name();
            BigDecimal[] acumulado = consolidado.computeIfAbsent(clave,
                    k -> new BigDecimal[] {BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO});
            acumulado[0] = acumulado[0].add(BigDecimal.ONE);
            acumulado[1] = acumulado[1].add(o.getMonto());
            acumulado[2] = acumulado[2].add(o.getComision());
        }
        List<String[]> resumen = new ArrayList<>();
        for (Map.Entry<String, BigDecimal[]> e : consolidado.entrySet()) {
            String[] partes = e.getKey().split("\\|");
            resumen.add(new String[] {partes[0], TipoOperacion.valueOf(partes[1]).getEtiqueta(),
                    e.getValue()[0].toPlainString(), moneda(e.getValue()[1]), moneda(e.getValue()[2])});
        }
        if (resumen.isEmpty()) {
            pdf.parrafo("Sin datos para consolidar.");
        } else {
            pdf.tabla(new String[] {"Mes", "Tipo", "Cantidad", "Monto total", "Comisión total"},
                    new float[] {15, 20, 15, 25, 25}, resumen, Set.of(2, 3, 4), false);
        }
        return pdf.terminar();
    }

    public byte[] reporteInmuebles(List<Inmueble> inmuebles) {
        DocumentoPdf pdf = nuevo(true);
        pdf.titulo("Cartera de inmuebles");
        pdf.subtitulo(inmuebles.size() + " inmuebles registrados · Generado el " + FECHA_HORA.format(LocalDateTime.now()));
        List<String[]> filas = new ArrayList<>();
        for (Inmueble in : inmuebles) {
            filas.add(new String[] {String.valueOf(in.getIdInmueble()), in.getTipo().getEtiqueta(), in.getOperacionOfrecida().getEtiqueta(),
                    in.getDistrito(), in.getDireccion(), in.getMetraje().toPlainString(), moneda(in.getPrecio()),
                    in.getNombrePropietario(), in.getEstado().getEtiqueta()});
        }
        pdf.tabla(new String[] {"N.°", "Tipo", "Operación", "Distrito", "Dirección", "m²", "Precio", "Propietario", "Estado"},
                new float[] {4, 10, 8, 12, 18, 6, 11, 17, 10}, filas, Set.of(0, 5, 6), false);
        return pdf.terminar();
    }

    public byte[] listado(String titulo, String subtitulo, String[] encabezados, float[] proporciones, List<String[]> filas) {
        DocumentoPdf pdf = nuevo(true);
        pdf.titulo(titulo);
        pdf.subtitulo(subtitulo + " · Generado el " + FECHA_HORA.format(LocalDateTime.now()));
        if (filas.isEmpty()) {
            pdf.parrafo("No hay registros.");
        } else {
            pdf.tabla(encabezados, proporciones, filas, Set.of(), false);
        }
        return pdf.terminar();
    }

    public byte[] contrato(Operacion op, Inmueble in) {
        boolean venta = op.getTipo() == TipoOperacion.VENTA;
        DocumentoPdf pdf = nuevo(false);
        pdf.titulo(venta ? "CONTRATO DE COMPRAVENTA DE INMUEBLE" : "CONTRATO DE ARRENDAMIENTO DE INMUEBLE");
        pdf.subtitulo("Operación N.° " + op.getIdOperacion() + " · Arequipa, " + FECHA.format(op.getFechaInicio()));
        pdf.espacio(8);
        String parte = venta ? "EL COMPRADOR" : "EL ARRENDATARIO";
        String duenio = venta ? "EL VENDEDOR" : "EL ARRENDADOR";
        pdf.parrafo("Conste por el presente documento el contrato que celebran, de una parte, " + op.getNombrePropietario()
                + ", a quien en adelante se denominará " + duenio + "; y, de la otra parte, " + op.getClienteNombre()
                + ", identificado(a) con DNI N.° " + op.getClienteDni() + ", a quien en adelante se denominará " + parte
                + "; con la intermediación de " + EMPRESA + ", representada por su agente inmobiliario "
                + op.getNombreAgente() + "; en los términos y condiciones siguientes:");
        pdf.parrafoNegrita("PRIMERA: DEL INMUEBLE");
        pdf.parrafo(duenio + " es propietario del inmueble de tipo " + in.getTipo().getEtiqueta().toLowerCase(Locale.ROOT)
                + " ubicado en " + in.getDireccion() + ", distrito de " + in.getDistrito() + ", provincia y departamento de Arequipa, con un área de "
                + in.getMetraje().toPlainString() + " m²" + (in.getHabitaciones() != null && in.getHabitaciones() > 0 ? ", " + in.getHabitaciones() + " habitaciones" : "")
                + (in.isCochera() ? " y cochera" : "") + ".");
        pdf.parrafoNegrita("SEGUNDA: DEL OBJETO");
        pdf.parrafo(venta
                ? duenio + " transfiere en venta real y enajenación perpetua a favor de " + parte + " el inmueble descrito en la cláusula primera."
                : duenio + " cede en arrendamiento a " + parte + " el inmueble descrito en la cláusula primera, para uso exclusivo de vivienda o del giro declarado.");
        pdf.parrafoNegrita(venta ? "TERCERA: DEL PRECIO" : "TERCERA: DE LA RENTA Y EL PLAZO");
        pdf.parrafo(venta
                ? "Las partes acuerdan como precio de venta la suma de " + moneda(op.getMonto()) + ", que " + parte + " cancela a la firma del presente contrato."
                : "La renta mensual asciende a " + moneda(op.getMonto()) + ", pagadera por adelantado dentro de los cinco primeros días de cada mes. El plazo del arrendamiento rige del "
                  + FECHA.format(op.getFechaInicio()) + " al " + fecha(op.getFechaFin()) + ".");
        pdf.parrafoNegrita("CUARTA: DE LA INTERMEDIACIÓN");
        pdf.parrafo(EMPRESA + " intervino como intermediaria y percibe una comisión de " + moneda(op.getComision())
                + (venta ? ", equivalente al 3 % del precio de venta." : ", equivalente a un mes de renta."));
        pdf.parrafoNegrita("QUINTA: DE LA ENTREGA Y CONFORMIDAD");
        pdf.parrafo("El inmueble se entrega en el estado en que se encuentra, que " + parte + " declara conocer y aceptar. Las partes declaran que en la celebración del presente contrato no ha mediado error, dolo ni vicio alguno que pudiera invalidarlo.");
        pdf.parrafoNegrita("SEXTA: DE LA JURISDICCIÓN");
        pdf.parrafo("Para cualquier controversia derivada del presente contrato, las partes se someten a los jueces y tribunales de la ciudad de Arequipa.");
        pdf.firmas(duenio, parte, EMPRESA);
        return pdf.terminar();
    }

    private static DocumentoPdf nuevo(boolean horizontal) {
        return new DocumentoPdf(EMPRESA + " · Portal web de gestión inmobiliaria", horizontal);
    }

    private static String fecha(LocalDate fecha) {
        return fecha == null ? "—" : FECHA.format(fecha);
    }

    static String moneda(BigDecimal valor) {
        DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "S/ " + formato.format(valor == null ? BigDecimal.ZERO : valor);
    }
}
