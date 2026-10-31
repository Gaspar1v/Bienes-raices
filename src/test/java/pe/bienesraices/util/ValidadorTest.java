package pe.bienesraices.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ValidadorTest {
    @Test
    void datosCorrectosSonValidos() {
        Validador v = new Validador()
                .requerido("nombre", "Ana Torres", "El nombre")
                .email("email", "ana.torres@correo.pe")
                .dni("dni", "45678912")
                .telefono("telefono", "987654321")
                .positivo("precio", new BigDecimal("1500"), "El precio");
        assertTrue(v.esValido());
    }

    @Test
    void detectaCamposObligatoriosVacios() {
        Validador v = new Validador().requerido("nombre", "   ", "El nombre").requerido("tipo", null, "El tipo");
        assertEquals(2, v.getErrores().size());
        assertEquals("El nombre es obligatorio.", v.getErrores().get("nombre"));
    }

    @Test
    void detectaFormatosInvalidos() {
        Validador v = new Validador()
                .email("email", "ana@@correo")
                .dni("dni", "4567A912")
                .telefono("telefono", "054123456")
                .positivo("precio", new BigDecimal("-10"), "El precio")
                .rango("habitaciones", 99, 0, 50, "Las habitaciones")
                .longitudMaxima("distrito", "x".repeat(61), 60, "El distrito");
        assertFalse(v.esValido());
        assertEquals(6, v.getErrores().size());
    }

    @Test
    void enmascaraDatosPersonalesEnLogs() {
        assertEquals("****8912", Auditoria.enmascarar("45678912"));
        assertEquals("****", Auditoria.enmascarar("12"));
    }

    @Test
    void eliminaSaltosDeLineaParaEvitarInyeccionEnLogs() {
        assertFalse(Auditoria.limpiar("admin\nLOGIN_EXITOSO falso").contains("\n"));
    }
}
