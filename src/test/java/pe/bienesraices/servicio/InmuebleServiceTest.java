package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.impl.InmuebleDAOImpl;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;
import pe.bienesraices.util.Validador;

class InmuebleServiceTest {
    private static final UsuarioSesion ASISTENTE = new UsuarioSesion(4, "asistente", "Lucia Ccama", Rol.ASISTENTE);

    private InmuebleService servicio;

    @BeforeEach
    void preparar() throws Exception {
        servicio = new InmuebleService(new InmuebleDAOImpl(BaseDatosPrueba.nueva()));
    }

    private static Inmueble terreno() {
        Inmueble in = new Inmueble();
        in.setTipo(TipoPropiedad.TERRENO);
        in.setOperacionOfrecida(TipoOperacion.VENTA);
        in.setMetraje(new BigDecimal("250"));
        in.setDistrito("Cerro Colorado");
        in.setDireccion("Av. Aviacion km 5");
        in.setPrecio(new BigDecimal("180000"));
        in.setIdPropietario(1);
        return in;
    }

    @Test
    void unTerrenoNoPuedeTenerHabitaciones() {
        Inmueble in = terreno();
        in.setHabitaciones(3);
        Validador v = new Validador();
        servicio.validar(in, v);
        assertEquals("Un terreno no tiene habitaciones.", v.getErrores().get("habitaciones"));
    }

    @Test
    void registrarDejaElInmuebleComoNuevoIngresoConHistorial() {
        int id = servicio.registrar(terreno(), ASISTENTE);
        assertEquals(EstadoInmueble.NUEVO_INGRESO, servicio.buscarPorId(id).orElseThrow().getEstado());
        assertEquals(1, servicio.historial(id).size());
    }

    @Test
    void registrarRechazaDatosInvalidos() {
        Inmueble in = terreno();
        in.setPrecio(new BigDecimal("-5"));
        ValidacionException e = assertThrows(ValidacionException.class, () -> servicio.registrar(in, ASISTENTE));
        assertTrue(e.getErrores().containsKey("precio"));
    }

    @Test
    void preconditionsDeGuavaRechazanArgumentosInvalidos() {
        assertThrows(IllegalArgumentException.class, () -> servicio.cambiarEstado(0, EstadoInmueble.EN_RESERVA, ASISTENTE));
        assertThrows(NullPointerException.class, () -> servicio.registrar(terreno(), null));
    }

    @Test
    void laListaDePropietariosSeSirveDesdeLaCache() {
        List<?> primera = servicio.propietarios();
        assertEquals(3, primera.size());

        assertTrue(primera == servicio.propietarios());
    }

    @Test
    void soloAsistenteYAdministradorEditanInmuebles() {
        assertTrue(Permisos.puedeAcceder(Rol.GERENTE, "/app/inmuebles"));
        assertTrue(Permisos.puedeEditarInmuebles(Rol.ASISTENTE));
        assertTrue(Permisos.puedeEditarInmuebles(Rol.ADMINISTRADOR));
        assertFalse(Permisos.puedeEditarInmuebles(Rol.GERENTE));
        assertFalse(Permisos.puedeEditarInmuebles(Rol.AGENTE));
    }
}
