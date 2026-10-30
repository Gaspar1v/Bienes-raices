package pe.bienesraices.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PasswordHasherTest {
    @Test
    void verificaLaContrasenaCorrecta() {
        String hash = PasswordHasher.hash("Clave#2026".toCharArray());
        assertTrue(PasswordHasher.verificar("Clave#2026".toCharArray(), hash));
    }

    @Test
    void rechazaUnaContrasenaIncorrecta() {
        String hash = PasswordHasher.hash("Clave#2026".toCharArray());
        assertFalse(PasswordHasher.verificar("clave#2026".toCharArray(), hash));
    }

    @Test
    void mismaContrasenaGeneraHashesDistintosPorLaSal() {
        assertNotEquals(PasswordHasher.hash("Clave#2026".toCharArray()), PasswordHasher.hash("Clave#2026".toCharArray()));
    }

    @Test
    void rechazaHashConFormatoInvalido() {
        assertFalse(PasswordHasher.verificar("x".toCharArray(), "texto-plano"));
        assertFalse(PasswordHasher.verificar("x".toCharArray(), "pbkdf2$abc$zz$zz"));
    }
}
