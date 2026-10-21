package pe.bienesraices.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

public final class Configuracion {
    private static final Configuracion INSTANCIA = new Configuracion();

    private final Properties propiedades = new Properties();

    private Configuracion() {
        try (InputStream in = Configuracion.class.getResourceAsStream("/config.properties")) {
            if (in != null) {
                propiedades.load(in);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer config.properties", e);
        }
    }

    public static Configuracion get() {
        return INSTANCIA;
    }

    public String valor(String clave) {
        String variable = "BR_" + clave.replace('.', '_').toUpperCase(Locale.ROOT);
        String desdeEntorno = System.getenv(variable);
        if (StringUtils.isNotBlank(desdeEntorno)) {
            return desdeEntorno;
        }
        String desdeSistema = System.getProperty(clave);
        if (StringUtils.isNotBlank(desdeSistema)) {
            return desdeSistema;
        }
        return propiedades.getProperty(clave, "");
    }

    public boolean activado(String clave) {
        return Boolean.parseBoolean(valor(clave));
    }
}
