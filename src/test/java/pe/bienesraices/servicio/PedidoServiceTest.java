package pe.bienesraices.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.bienesraices.BaseDatosPrueba;
import pe.bienesraices.dao.impl.PedidoDAOImpl;
import pe.bienesraices.modelo.Pedido;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

class PedidoServiceTest {
    private PedidoService servicio;

    @BeforeEach
    void preparar() throws Exception {
        servicio = new PedidoService(new PedidoDAOImpl(BaseDatosPrueba.nueva()));
    }

    private static Pedido pedido() {
        Pedido p = new Pedido();
        p.setTipoOperacion(TipoOperacion.VENTA);
        p.setTipoPropiedad(TipoPropiedad.TERRENO);
        p.setDistrito("Socabaya");
        p.setPrecioMaximo(new BigDecimal("150000"));
        p.setNombreContacto("Ana Torres Vilca");
        p.setEmail("ana.torres@correo.pe");
        p.setTelefono("987111222");
        return p;
    }

    @Test
    void registraElPedidoComoPendiente() {
        servicio.registrar(pedido());
        assertEquals(2, servicio.listar().size());
        assertTrue(servicio.listar().stream().allMatch(p -> "PENDIENTE".equals(p.getEstado())));
    }

    @Test
    void rechazaContactoInvalido() {
        Pedido p = pedido();
        p.setEmail("ana@@correo");
        p.setTelefono("054123456");
        ValidacionException e = assertThrows(ValidacionException.class, () -> servicio.registrar(p));
        assertTrue(e.getErrores().containsKey("email"));
        assertTrue(e.getErrores().containsKey("telefono"));
    }
}
