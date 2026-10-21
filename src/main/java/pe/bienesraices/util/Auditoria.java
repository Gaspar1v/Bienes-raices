package pe.bienesraices.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Auditoria {
    private static final Logger LOG = LoggerFactory.getLogger("AUDITORIA");

    private Auditoria() {
    }

    public static void registrar(String accion, String detalle) {
        LOG.info("accion={} detalle=\"{}\"", accion, limpiar(detalle));
    }

    public static String limpiar(String texto) {
        if (texto == null) {
            return "";
        }
        return StringUtils.replaceChars(texto, "\r\n\t", "   ");
    }

    public static String enmascarar(String dato) {
        if (StringUtils.length(dato) <= 4) {
            return "****";
        }
        return StringUtils.leftPad(StringUtils.right(dato, 4), dato.length(), '*');
    }
}
