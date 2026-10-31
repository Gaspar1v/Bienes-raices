package pe.bienesraices.servicio;

import java.util.Map;

public class ValidacionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final transient Map<String, String> errores;

    public ValidacionException(Map<String, String> errores) {
        super("Datos invalidos: " + errores.keySet());
        this.errores = Map.copyOf(errores);
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
