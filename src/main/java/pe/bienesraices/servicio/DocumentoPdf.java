package pe.bienesraices.servicio;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

final class DocumentoPdf {
    private static final float MARGEN = 50;
    private static final float PIE = 40;
    private static final Color PRIMARIO = new Color(0x1F, 0x6F, 0x6A);
    private static final Color GRIS = new Color(0x66, 0x73, 0x7A);
    private static final Color LINEA = new Color(0xBF, 0xC8, 0xC6);
    private static final Color FONDO_TOTAL = new Color(0xEE, 0xF2, 0xF0);

    private final PDDocument doc = new PDDocument();
    private final PDType1Font normal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
    private final PDType1Font negrita = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
    private final String pieDePagina;
    private final PDRectangle tamano;
    private final float anchoUtil;
    private PDPageContentStream cs;
    private float y;

    DocumentoPdf(String pieDePagina, boolean horizontal) {
        this.pieDePagina = pieDePagina;
        this.tamano = horizontal ? new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth()) : PDRectangle.A4;
        this.anchoUtil = tamano.getWidth() - 2 * MARGEN;
        nuevaPagina();
    }

    void titulo(String texto) {
        escribirLineas(texto, negrita, 16, PRIMARIO, 0, anchoUtil, 20);
        espacio(4);
    }

    void subtitulo(String texto) {
        escribirLineas(texto, normal, 10, GRIS, 0, anchoUtil, 14);
    }

    void seccion(String texto) {
        espacio(10);
        escribirLineas(texto, negrita, 12, PRIMARIO, 0, anchoUtil, 16);
        espacio(2);
    }

    void parrafo(String texto) {
        escribirLineas(texto, normal, 10.5f, Color.BLACK, 0, anchoUtil, 15);
        espacio(6);
    }

    void parrafoNegrita(String texto) {
        escribirLineas(texto, negrita, 10.5f, Color.BLACK, 0, anchoUtil, 15);
        espacio(6);
    }

    void espacio(float puntos) {
        y -= puntos;
    }

    void tabla(String[] encabezados, float[] proporciones, List<String[]> filas, Set<Integer> numericas, boolean ultimaEsTotal) {
        float[] anchos = new float[proporciones.length];
        float suma = 0;
        for (float p : proporciones) {
            suma += p;
        }
        for (int i = 0; i < anchos.length; i++) {
            anchos[i] = anchoUtil * proporciones[i] / suma;
        }
        filaTabla(encabezados, anchos, numericas, true, false);
        for (int i = 0; i < filas.size(); i++) {
            boolean total = ultimaEsTotal && i == filas.size() - 1;
            if (necesitaSalto(altoFila(filas.get(i), anchos, total ? negrita : normal))) {
                nuevaPagina();
                filaTabla(encabezados, anchos, numericas, true, false);
            }
            filaTabla(filas.get(i), anchos, numericas, false, total);
        }
        espacio(8);
    }

    void firmas(String... nombres) {
        espacio(50);
        if (necesitaSalto(40)) {
            nuevaPagina();
            espacio(50);
        }
        float ancho = anchoUtil / nombres.length;
        try {
            for (int i = 0; i < nombres.length; i++) {
                float x = MARGEN + i * ancho + 15;
                cs.setStrokingColor(Color.BLACK);
                cs.setLineWidth(0.6f);
                cs.moveTo(x, y);
                cs.lineTo(x + ancho - 30, y);
                cs.stroke();
                texto(nombres[i], normal, 9, Color.BLACK, x, y - 12);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        y -= 20;
    }

    byte[] terminar() {
        try {
            cs.close();
            int total = doc.getNumberOfPages();
            for (int i = 0; i < total; i++) {
                PDPage pagina = doc.getPage(i);
                try (PDPageContentStream pie = new PDPageContentStream(doc, pagina, PDPageContentStream.AppendMode.APPEND, true, true)) {
                    String izquierda = limpiar(pieDePagina, normal);
                    String derecha = "Página " + (i + 1) + " de " + total;
                    pie.beginText();
                    pie.setFont(normal, 8);
                    pie.setNonStrokingColor(GRIS);
                    pie.newLineAtOffset(MARGEN, PIE - 15);
                    pie.showText(izquierda);
                    pie.endText();
                    pie.beginText();
                    pie.setFont(normal, 8);
                    pie.newLineAtOffset(MARGEN + anchoUtil - ancho(derecha, normal, 8), PIE - 15);
                    pie.showText(limpiar(derecha, normal));
                    pie.endText();
                }
            }
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            doc.save(salida);
            doc.close();
            return salida.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo generar el PDF", e);
        }
    }

    private void filaTabla(String[] celdas, float[] anchos, Set<Integer> numericas, boolean encabezado, boolean total) {
        PDType1Font fuente = encabezado || total ? negrita : normal;
        float alto = altoFila(celdas, anchos, fuente);
        try {
            if (encabezado || total) {
                cs.setNonStrokingColor(encabezado ? PRIMARIO : FONDO_TOTAL);
                cs.addRect(MARGEN, y - alto, anchoUtil, alto);
                cs.fill();
            }
            float x = MARGEN;
            for (int i = 0; i < celdas.length; i++) {
                List<String> lineas = partir(celdas[i], fuente, 8.5f, anchos[i] - 8);
                float ly = y - 11;
                for (String l : lineas) {
                    float lx = numericas.contains(i) ? x + anchos[i] - 4 - ancho(l, fuente, 8.5f) : x + 4;
                    texto(l, fuente, 8.5f, encabezado ? Color.WHITE : Color.BLACK, lx, ly);
                    ly -= 10.5f;
                }
                x += anchos[i];
            }
            cs.setStrokingColor(LINEA);
            cs.setLineWidth(0.5f);
            cs.moveTo(MARGEN, y - alto);
            cs.lineTo(MARGEN + anchoUtil, y - alto);
            cs.stroke();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        y -= alto;
    }

    private float altoFila(String[] celdas, float[] anchos, PDType1Font fuente) {
        int maximo = 1;
        for (int i = 0; i < celdas.length; i++) {
            maximo = Math.max(maximo, partir(celdas[i], fuente, 8.5f, anchos[i] - 8).size());
        }
        return 6 + maximo * 10.5f;
    }

    private void escribirLineas(String texto, PDType1Font fuente, float tamano, Color color, float sangria, float ancho, float interlineado) {
        for (String linea : partir(texto, fuente, tamano, ancho - sangria)) {
            if (necesitaSalto(interlineado)) {
                nuevaPagina();
            }
            y -= interlineado;
            texto(linea, fuente, tamano, color, MARGEN + sangria, y + 4);
        }
    }

    private void texto(String texto, PDType1Font fuente, float tamano, Color color, float x, float yTexto) {
        try {
            cs.beginText();
            cs.setFont(fuente, tamano);
            cs.setNonStrokingColor(color);
            cs.newLineAtOffset(x, yTexto);
            cs.showText(texto);
            cs.endText();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private List<String> partir(String texto, PDType1Font fuente, float tamano, float ancho) {
        List<String> lineas = new ArrayList<>();
        String limpio = limpiar(texto == null ? "" : texto, fuente);
        StringBuilder actual = new StringBuilder();
        for (String palabra : limpio.split(" ")) {
            String prueba = actual.length() == 0 ? palabra : actual + " " + palabra;
            if (ancho(prueba, fuente, tamano) <= ancho) {
                actual.setLength(0);
                actual.append(prueba);
                continue;
            }
            if (actual.length() > 0) {
                lineas.add(actual.toString());
                actual.setLength(0);
            }
            while (ancho(palabra, fuente, tamano) > ancho && palabra.length() > 1) {
                int corte = palabra.length() - 1;
                while (corte > 1 && ancho(palabra.substring(0, corte), fuente, tamano) > ancho) {
                    corte--;
                }
                lineas.add(palabra.substring(0, corte));
                palabra = palabra.substring(corte);
            }
            actual.append(palabra);
        }
        lineas.add(actual.toString());
        return lineas;
    }

    private static float ancho(String texto, PDType1Font fuente, float tamano) {
        try {
            return fuente.getStringWidth(texto) / 1000 * tamano;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static String limpiar(String texto, PDType1Font fuente) {
        StringBuilder sb = new StringBuilder(texto.length());
        for (char c : texto.replace('\n', ' ').replace('\t', ' ').toCharArray()) {
            try {
                fuente.encode(String.valueOf(c));
                sb.append(c);
            } catch (IOException | IllegalArgumentException e) {
                sb.append('?');
            }
        }
        return sb.toString();
    }

    private boolean necesitaSalto(float alto) {
        return y - alto < MARGEN + PIE;
    }

    private void nuevaPagina() {
        try {
            if (cs != null) {
                cs.close();
            }
            PDPage pagina = new PDPage(tamano);
            doc.addPage(pagina);
            cs = new PDPageContentStream(doc, pagina);
            y = tamano.getHeight() - MARGEN;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
