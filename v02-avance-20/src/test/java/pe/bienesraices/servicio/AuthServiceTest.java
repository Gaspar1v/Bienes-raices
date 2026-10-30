package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.impl.UsuarioDAOImpl;
import pe.bienesraices.modelo.Rol;

class AuthServiceTest {
    private AuthService servicio;

    @BeforeEach
    void preparar() throws Exception {
        Clock reloj = Clock.fixed(Instant.parse("2026-10-20T15:00:00Z"), ZoneId.of("America/Lima"));
        servicio = new AuthService(new UsuarioDAOImpl(BaseDatosPrueba.nueva()), reloj);
    }

    @Test
    void autenticaConCredencialesCorrectas() {
        AuthService.Resultado r = servicio.autenticar("gerente", "Gerente#2026".toCharArray());
        assertEquals(AuthService.Estado.EXITOSO, r.estado());
        assertEquals(Rol.GERENTE, r.usuario().getRol());
    }

    @Test
    void mensajeIgualParaUsuarioInexistenteYClaveIncorrecta() {
        assertEquals(AuthService.Estado.CREDENCIALES_INVALIDAS, servicio.autenticar("noexiste", "x".toCharArray()).estado());
        assertEquals(AuthService.Estado.CREDENCIALES_INVALIDAS, servicio.autenticar("gerente", "x".toCharArray()).estado());
    }

    @Test
    void bloqueaLaCuentaTrasCincoIntentosFallidos() {
        for (int i = 1; i < AuthService.MAX_INTENTOS; i++) {
            servicio.autenticar("agente", "incorrecta".toCharArray());
        }
        AuthService.Resultado quinto = servicio.autenticar("agente", "incorrecta".toCharArray());
        assertEquals(AuthService.Estado.CUENTA_BLOQUEADA, quinto.estado());

        AuthService.Resultado correcto = servicio.autenticar("agente", "Agente#2026".toCharArray());
        assertEquals(AuthService.Estado.CUENTA_BLOQUEADA, correcto.estado());
        assertNull(correcto.usuario());
    }
}
