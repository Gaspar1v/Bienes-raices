package pe.bienesraices.controlador;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import jakarta.servlet.http.HttpServletRequest;
import pe.bienesraices.util.Validador;

final class Formulario {
    private final HttpServletRequest req;
    private final Validador validador = new Validador();

    Formulario(HttpServletRequest req) {
        this.req = req;
    }

    Validador validador() {
        return validador;
    }

    String texto(String nombre) {
        return StringUtils.trimToNull(StringUtils.normalizeSpace(req.getParameter(nombre)));
    }

    BigDecimal decimal(String nombre, String etiqueta) {
        String valor = texto(nombre);
        if (valor == null) {
            return null;
        }
        if (!NumberUtils.isParsable(valor)) {
            validador.agregar(nombre, etiqueta + " debe ser un número válido.");
            return null;
        }
        return new BigDecimal(valor);
    }

    Integer entero(String nombre, String etiqueta) {
        String valor = texto(nombre);
        if (valor == null) {
            return null;
        }
        try {
            return Integer.valueOf(valor);
        } catch (NumberFormatException e) {
            validador.agregar(nombre, etiqueta + " debe ser un número entero.");
            return null;
        }
    }

    LocalDate fecha(String nombre, String etiqueta) {
        String valor = texto(nombre);
        if (valor == null) {
            return null;
        }
        try {
            return LocalDate.parse(valor);
        } catch (DateTimeParseException e) {
            validador.agregar(nombre, etiqueta + " no es una fecha válida.");
            return null;
        }
    }

    <E extends Enum<E>> E enumeracion(String nombre, Class<E> tipo) {
        return EnumUtils.getEnumIgnoreCase(tipo, texto(nombre));
    }

    boolean casilla(String nombre) {
        String valor = req.getParameter(nombre);
        return "on".equals(valor) || "true".equals(valor);
    }

    int id(String nombre) {
        return NumberUtils.toInt(req.getParameter(nombre), 0);
    }
}
