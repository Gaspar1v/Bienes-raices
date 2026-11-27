package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.impl.UsuarioDAOImpl;
import pe.bienesraices.modelo.ResumenAgente;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.UsuarioSesion;

class UsuarioServiceTest {
    private static final UsuarioSesion ADMIN = new UsuarioSesion(1, "admin", "Administrador", Rol.ADMINISTRADOR);

    private UsuarioService servicio;
    private AuthService auth;

    @BeforeEach
    void preparar() throws Exception {
        FabricaConexion bd = BaseDatosPrueba.nueva();
        servicio = new UsuarioService(new UsuarioDAOImpl(bd));
        auth = new AuthService(new UsuarioDAOImpl(bd), Clock.systemDefaultZone());
    }

    @Test
    void crearUnAgentePermiteIniciarSesion() {
        servicio.crear("pedro.ramos", "Pedro Ramos Calle", Rol.AGENTE, "Agente2026".toCharArray(), ADMIN);
        AuthService.Resultado r = auth.autenticar("pedro.ramos", "Agente2026".toCharArray());
        assertEquals(AuthService.Estado.EXITOSO, r.estado());
        assertEquals(Rol.AGENTE, r.usuario().getRol());
    }

    @Test
    void rechazaUsuarioRepetidoYClaveDebil() {
        ValidacionException e = assertThrows(ValidacionException.class,
                () -> servicio.crear("agente", "Otro Agente", Rol.AGENTE, "corta".toCharArray(), ADMIN));
        assertTrue(e.getErrores().containsKey("username"));
        assertTrue(e.getErrores().containsKey("clave"));
    }

    @Test
    void soloSeCreanCuentasDelPersonalInterno() {
        ValidacionException e = assertThrows(ValidacionException.class,
                () -> servicio.crear("cliente1", "Un Cliente", Rol.CLIENTE, "Cliente2026".toCharArray(), ADMIN));
        assertTrue(e.getErrores().containsKey("rol"));
    }

    @Test
    void unUsuarioDesactivadoNoPuedeIngresar() {
        servicio.actualizar(3, "Carlos Huaman Flores", Rol.AGENTE, false, ADMIN);
        assertFalse(servicio.buscarPorId(3).orElseThrow().isActivo());
        assertEquals(AuthService.Estado.CREDENCIALES_INVALIDAS, auth.autenticar("agente", "Agente#2026".toCharArray()).estado());
    }

    @Test
    void elAdministradorNoPuedeDesactivarseASiMismo() {
        assertThrows(ValidacionException.class, () -> servicio.actualizar(1, "Administrador", Rol.ADMINISTRADOR, false, ADMIN));
        assertThrows(ValidacionException.class, () -> servicio.actualizar(1, "Administrador", Rol.GERENTE, true, ADMIN));
    }

    @Test
    void restablecerLaClaveCambiaElAcceso() {
        servicio.restablecerClave(4, "Nueva2026x".toCharArray(), ADMIN);
        assertEquals(AuthService.Estado.EXITOSO, auth.autenticar("asistente", "Nueva2026x".toCharArray()).estado());
        assertEquals(AuthService.Estado.CREDENCIALES_INVALIDAS, auth.autenticar("asistente", "Asistente#2026".toCharArray()).estado());
    }

    @Test
    void elResumenDeAgentesSumaSusComisiones() {
        List<ResumenAgente> agentes = servicio.resumenAgentes();
        assertEquals(1, agentes.size());
        ResumenAgente carlos = agentes.get(0);
        assertEquals(2, carlos.operaciones());
        assertEquals(1, carlos.ventas());
        assertEquals(1, carlos.alquileres());
        assertEquals(0, new BigDecimal("13250.00").compareTo(carlos.comisionTotal()));
    }
}
