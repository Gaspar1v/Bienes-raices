package pe.bienesraices.controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

@WebServlet("/inicio")
public class PortalServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        CriterioBusqueda c = new CriterioBusqueda();
        c.setOperacion(f.enumeracion("operacion", TipoOperacion.class));
        c.setTipo(f.enumeracion("tipo", TipoPropiedad.class));
        c.setDistrito(f.texto("distrito"));
        c.setPrecioMaximo(f.decimal("precioMax", "El precio máximo"));
        c.setMetrajeMinimo(f.decimal("metrajeMin", "El metraje mínimo"));
        c.setHabitacionesMinimas(f.entero("habitaciones", "Las habitaciones"));
        c.setSoloDisponibles(true);

        req.setAttribute("criterio", c);
        req.setAttribute("errores", f.validador().getErrores());
        req.setAttribute("busco", req.getParameter("buscar") != null);
        req.setAttribute("inmuebles", servicios().inmuebles().buscar(c));
        vista("portal", req, res);
    }
}
