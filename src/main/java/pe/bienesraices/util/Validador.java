package pe.bienesraices.util;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.commons.validator.routines.EmailValidator;

public class Validador {
    private final Map<String, String> errores = new LinkedHashMap<>();

    public Validador requerido(String campo, Object valor, String etiqueta) {
        boolean vacio = valor == null || (valor instanceof CharSequence cs && StringUtils.isBlank(cs));
        if (vacio) {
            agregar(campo, etiqueta + " es obligatorio.");
        }
        return this;
    }

    public Validador longitudMaxima(String campo, String valor, int maximo, String etiqueta) {
        if (valor != null && !GenericValidator.maxLength(valor, maximo)) {
            agregar(campo, etiqueta + " no debe superar " + maximo + " caracteres.");
        }
        return this;
    }

    public Validador email(String campo, String valor) {
        if (StringUtils.isNotBlank(valor) && !EmailValidator.getInstance().isValid(valor)) {
            agregar(campo, "El correo electrónico no tiene un formato válido.");
        }
        return this;
    }

    public Validador dni(String campo, String valor) {
        if (StringUtils.isNotBlank(valor) && !(valor.length() == 8 && StringUtils.isNumeric(valor))) {
            agregar(campo, "El DNI debe tener 8 dígitos.");
        }
        return this;
    }

    public Validador telefono(String campo, String valor) {
        if (StringUtils.isNotBlank(valor) && !GenericValidator.matchRegexp(valor, "^9\\d{8}$")) {
            agregar(campo, "El teléfono debe tener 9 dígitos y empezar con 9.");
        }
        return this;
    }

    public Validador positivo(String campo, BigDecimal valor, String etiqueta) {
        if (valor != null && valor.signum() <= 0) {
            agregar(campo, etiqueta + " debe ser mayor que cero.");
        }
        return this;
    }

    public Validador rango(String campo, Integer valor, int min, int max, String etiqueta) {
        if (valor != null && !GenericValidator.isInRange(valor, min, max)) {
            agregar(campo, etiqueta + " debe estar entre " + min + " y " + max + ".");
        }
        return this;
    }

    public Validador regla(boolean cumple, String campo, String mensaje) {
        if (!cumple) {
            agregar(campo, mensaje);
        }
        return this;
    }

    public void agregar(String campo, String mensaje) {
        errores.putIfAbsent(campo, mensaje);
    }

    public boolean esValido() {
        return errores.isEmpty();
    }

    public Map<String, String> getErrores() {
        return Collections.unmodifiableMap(errores);
    }
}
