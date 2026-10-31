package pe.bienesraices.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.impl.InmuebleDAOImpl;
import pe.bienesraices.dao.impl.OperacionDAOImpl;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.HistorialEstado;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.servicio.OperacionService;

class OperacionDAOTest {
    private static final UsuarioSesion AGENTE = new UsuarioSesion(3, "agente", "Carlos Huaman", Rol.AGENTE);

    private InmuebleDAO inmuebles;
    private OperacionService operaciones;

    @BeforeEach
    void preparar() throws Exception {
        FabricaConexion bd = BaseDatosPrueba.nueva();
        inmuebles = new InmuebleDAOImpl(bd);
        operaciones = new OperacionService(new OperacionDAOImpl(bd));
    }

    private static Operacion venta(int idInmueble) {
        Operacion op = new Operacion();
        op.setIdInmueble(idInmueble);
        op.setTipo(TipoOperacion.VENTA);
        op.setClienteNombre("Rocio Paz Linares");
        op.setClienteDni("74561238");
        op.setFechaInicio(LocalDate.of(2026, 10, 20));
        op.setMonto(new BigDecimal("480000"));
        return op;
    }

    @Test
    void registrarVentaActualizaEstadoHistorialYComision() {
        operaciones.registrar(venta(1), AGENTE);

        Inmueble casa = inmuebles.buscarPorId(1).orElseThrow();
        assertEquals(EstadoInmueble.VENDIDO, casa.getEstado());

        List<HistorialEstado> historial = inmuebles.listarHistorial(1);
        assertEquals(EstadoInmueble.VENDIDO, historial.get(0).getEstadoNuevo());
        assertEquals("agente", historial.get(0).getUsuario());

        Operacion registrada = operaciones.listarPorRango(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 20)).get(0);
        assertEquals(0, new BigDecimal("14400.00").compareTo(registrada.getComision()));
    }

    @Test
    void inmuebleVendidoDejaDeAparecerEnLaBusquedaPublica() {
        operaciones.registrar(venta(1), AGENTE);
        List<Inmueble> disponibles = inmuebles.buscar(new CriterioBusqueda());
        assertTrue(disponibles.stream().noneMatch(i -> i.getIdInmueble() == 1));
    }

    @Test
    void noPermiteVenderDosVecesElMismoInmueble() {
        operaciones.registrar(venta(1), AGENTE);
        DAOException error = assertThrows(DAOException.class, () -> operaciones.registrar(venta(1), AGENTE));
        assertTrue(error.getMessage().contains("no está disponible"));

        assertEquals(1, operaciones.listarPorRango(LocalDate.of(2026, 10, 20), LocalDate.of(2026, 10, 20)).size());
    }

    @Test
    void busquedaConTextoMaliciosoNoAlteraLaConsulta() {
        CriterioBusqueda c = new CriterioBusqueda();
        c.setSoloDisponibles(false);
        c.setDistrito("' OR '1'='1");
        assertTrue(inmuebles.buscar(c).isEmpty());
    }
}
