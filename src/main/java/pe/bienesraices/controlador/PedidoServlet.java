package pe.bienesraices.controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.Pedido;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.servicio.ValidacionException;

@WebServlet("/pedido")
public class PedidoServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        Pedido p = new Pedido();
        p.setTipoOperacion(f.enumeracion("operacion", TipoOperacion.class));
        p.setTipoPropiedad(f.enumeracion("tipo", TipoPropiedad.class));
        p.setDistrito(f.texto("distrito"));
        p.setPrecioMaximo(f.decimal("precioMax", "El precio"));
        p.setMetrajeMinimo(f.decimal("metrajeMin", "El metraje"));
        p.setHabitacionesMinimas(f.entero("habitaciones", "Las habitaciones"));
        req.setAttribute("pedido", p);
        vista("pedido", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        Pedido p = new Pedido();
        p.setTipoOperacion(f.enumeracion("operacion", TipoOperacion.class));
        p.setTipoPropiedad(f.enumeracion("tipo", TipoPropiedad.class));
        p.setDistrito(f.texto("distrito"));
        p.setPrecioMaximo(f.decimal("precioMax", "El precio máximo"));
        p.setMetrajeMinimo(f.decimal("metrajeMin", "El metraje mínimo"));
        p.setHabitacionesMinimas(f.entero("habitaciones", "Las habitaciones"));
        p.setNombreContacto(f.texto("nombreContacto"));
        p.setEmail(f.texto("email"));
        p.setTelefono(f.texto("telefono"));

        servicios().pedidos().validar(p, f.validador());
        if (f.validador().esValido()) {
            try {
                servicios().pedidos().registrar(p);
                redirigir("/inicio", "Su pedido fue registrado. Un agente se comunicara con usted.", req, res);
                return;
            } catch (ValidacionException e) {
                e.getErrores().forEach(f.validador()::agregar);
            }
        }
        req.setAttribute("pedido", p);
        req.setAttribute("errores", f.validador().getErrores());
        vista("pedido", req, res);
    }
}
