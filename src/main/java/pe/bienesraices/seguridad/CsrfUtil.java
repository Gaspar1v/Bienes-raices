package pe.bienesraices.seguridad;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;

import jakarta.servlet.http.HttpSession;

public final class CsrfUtil {
    public static final String ATRIBUTO = "csrfToken";
    public static final String PARAMETRO = "_csrf";
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private CsrfUtil() {
    }

    public static String token(HttpSession sesion) {
        String token = (String) sesion.getAttribute(ATRIBUTO);
        if (token == null) {
            byte[] bytes = new byte[32];
            ALEATORIO.nextBytes(bytes);
            token = Base64.encodeBase64URLSafeString(bytes);
            sesion.setAttribute(ATRIBUTO, token);
        }
        return token;
    }

    public static boolean esValido(HttpSession sesion, String recibido) {
        if (sesion == null || recibido == null) {
            return false;
        }
        String esperado = (String) sesion.getAttribute(ATRIBUTO);
        return esperado != null && MessageDigest.isEqual(
                esperado.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8));
    }
}
