package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.impl.InmuebleDAOImpl;
import pe.bienesraices.dao.impl.SolicitudCaptacionDAOImpl;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.EstadoSolicitud;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.SolicitudCaptacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;

class CaptacionServiceTest {
    private static final UsuarioSesion AGENTE = new UsuarioSesion(3, "agente", "Carlos Huaman", Rol.AGENTE);

    private CaptacionService servicio;
    private InmuebleService inmuebles;

    @BeforeEach
    void preparar() throws Exception {
        FabricaConexion bd = BaseDatosPrueba.nueva();
        servicio = new CaptacionService(new SolicitudCaptacionDAOImpl(bd));
        inmuebles = new InmuebleService(new InmuebleDAOImpl(bd));
    }

    private static SolicitudCaptacion solicitud(String dni) {
        SolicitudCaptacion s = new SolicitudCaptacion();
        s.setOperacionOfrecida(TipoOperacion.ALQUILER);
        s.setTipo(TipoPropiedad.DEPARTAMENTO);
        s.setMetraje(new BigDecimal("72"));
        s.setDistrito("Miraflores");
        s.setDireccion("Av. Goyeneche 330 Dpto 201");
        s.setPrecio(new BigDecimal("1300"));
        s.setHabitaciones(2);
        s.setPiso(2);
        s.setPropietarioNombres("Rocio Llerena Paz");
        s.setPropietarioDni(dni);
        s.setPropietarioTelefono("951222333");
        s.setPropietarioEmail("rllerena@correo.pe");
        return s;
    }

    @Test
    void laSolicitudQuedaPendienteDeRevision() {
        int id = servicio.registrar(solicitud("44556677"));
        assertEquals(EstadoSolicitud.PENDIENTE, servicio.buscarPorId(id).orElseThrow().getEstado());
    }

    @Test
    void validaLosDatosDelPropietario() {
        SolicitudCaptacion s = solicitud("123");
        s.setPropietarioEmail("correo-invalido");
        ValidacionException e = assertThrows(ValidacionException.class, () -> servicio.registrar(s));
        assertTrue(e.getErrores().containsKey("propietarioDni"));
        assertTrue(e.getErrores().containsKey("propietarioEmail"));
    }

    @Test
    void aceptarIncorporaElInmuebleConUnPropietarioNuevo() {
        int id = servicio.registrar(solicitud("44556677"));
        int idInmueble = servicio.aceptar(id, AGENTE);

        Inmueble nuevo = inmuebles.buscarPorId(idInmueble).orElseThrow();
        assertEquals(EstadoInmueble.NUEVO_INGRESO, nuevo.getEstado());
        assertEquals("Rocio Llerena Paz", nuevo.getNombrePropietario());
        assertEquals(4, inmuebles.propietarios().size());

        SolicitudCaptacion aceptada = servicio.buscarPorId(id).orElseThrow();
        assertEquals(EstadoSolicitud.ACEPTADA, aceptada.getEstado());
        assertEquals(idInmueble, aceptada.getIdInmueble());
        assertEquals("agente", aceptada.getRevisor());
    }

    @Test
    void reutilizaAlPropietarioRegistradoPorDni() {
        int id = servicio.registrar(solicitud("41234567"));
        int idInmueble = servicio.aceptar(id, AGENTE);
        assertEquals("Maria Zegarra Rivera", inmuebles.buscarPorId(idInmueble).orElseThrow().getNombrePropietario());
    }

    @Test
    void noSePuedeAceptarDosVeces() {
        int id = servicio.registrar(solicitud("44556677"));
        servicio.aceptar(id, AGENTE);
        assertThrows(DAOException.class, () -> servicio.aceptar(id, AGENTE));
        assertThrows(DAOException.class, () -> servicio.observar(id, "Falta partida registral", AGENTE));
    }

    @Test
    void observarRequiereElMotivo() {
        int id = servicio.registrar(solicitud("44556677"));
        assertThrows(ValidacionException.class, () -> servicio.observar(id, " ", AGENTE));
        servicio.observar(id, "Falta copia literal de SUNARP", AGENTE);
        SolicitudCaptacion s = servicio.buscarPorId(id).orElseThrow();
        assertEquals(EstadoSolicitud.OBSERVADA, s.getEstado());
        assertEquals("Falta copia literal de SUNARP", s.getObservaciones());
    }
}
