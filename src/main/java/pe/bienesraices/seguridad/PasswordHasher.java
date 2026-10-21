package pe.bienesraices.seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

public final class PasswordHasher {
    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final int ITERACIONES = 210_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    private PasswordHasher() {
    }

    public static String hash(char[] password) {
        byte[] sal = new byte[BYTES_SAL];
        ALEATORIO.nextBytes(sal);
        byte[] hash = derivar(password, sal, ITERACIONES);
        return "pbkdf2$" + ITERACIONES + "$" + Hex.encodeHexString(sal) + "$" + Hex.encodeHexString(hash);
    }

    public static boolean verificar(char[] password, String almacenado) {
        String[] partes = StringUtils.split(almacenado, '$');
        if (partes == null || partes.length != 4 || !"pbkdf2".equals(partes[0])) {
            return false;
        }
        try {
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Hex.decodeHex(partes[2]);
            byte[] esperado = Hex.decodeHex(partes[3]);
            byte[] calculado = derivar(password, sal, iteraciones);

            return MessageDigest.isEqual(esperado, calculado);
        } catch (NumberFormatException | DecoderException e) {
            return false;
        }
    }

    private static byte[] derivar(char[] password, byte[] sal, int iteraciones) {
        KeySpec spec = new PBEKeySpec(password, sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no disponible en la JVM", e);
        }
    }

    public static void main(String[] args) {
        for (String clave : args) {
            System.out.println(hash(clave.toCharArray()));
        }
    }
}
