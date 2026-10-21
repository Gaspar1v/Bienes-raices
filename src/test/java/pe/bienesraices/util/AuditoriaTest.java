package pe.bienesraices.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

class AuditoriaTest {
    @Test
    void enmascaraDatosPersonalesEnLogs() {
        assertEquals("****8912", Auditoria.enmascarar("45678912"));
        assertEquals("****", Auditoria.enmascarar("12"));
    }

    @Test
    void eliminaSaltosDeLineaParaEvitarInyeccionEnLogs() {
        assertFalse(Auditoria.limpiar("admin\nLOGIN_EXITOSO falso").contains("\n"));
        assertEquals("", Auditoria.limpiar(null));
    }
}
