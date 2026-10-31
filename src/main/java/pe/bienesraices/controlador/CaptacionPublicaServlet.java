package pe.bienesraices.controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.SolicitudCaptacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.servicio.ValidacionException;

@WebServlet("/captacion")
public class CaptacionPublicaServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        req.setAttribute("solicitud", new SolicitudCaptacion());
        vista("captacion", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        SolicitudCaptacion s = leer(f);
        servicios().captacion().validar(s, f.validador());
        if (f.validador().esValido()) {
            try {
                servicios().captacion().registrar(s);
                redirigir("/inicio", "Su solicitud fue registrada y quedó pendiente de revisión. Un agente se comunicará con usted.", req, res);
                return;
            } catch (ValidacionException e) {
                e.getErrores().forEach(f.validador()::agregar);
            }
        }
        req.setAttribute("solicitud", s);
        req.setAttribute("errores", f.validador().getErrores());
        vista("captacion", req, res);
    }

    private static SolicitudCaptacion leer(Formulario f) {
        SolicitudCaptacion s = new SolicitudCaptacion();
        s.setOperacionOfrecida(f.enumeracion("operacionOfrecida", TipoOperacion.class));
        s.setTipo(f.enumeracion("tipo", TipoPropiedad.class));
        s.setMetraje(f.decimal("metraje", "El metraje"));
        s.setDistrito(f.texto("distrito"));
        s.setDireccion(f.texto("direccion"));
        s.setPrecio(f.decimal("precio", "El precio"));
        s.setHabitaciones(f.entero("habitaciones", "Las habitaciones"));
        s.setPiso(f.entero("piso", "El piso"));
        s.setCochera(f.casilla("cochera"));
        s.setAcabados(f.texto("acabados"));
        s.setReferencias(f.texto("referencias"));
        s.setFechaConstruccion(f.fecha("fechaConstruccion", "La fecha de construcción"));
        s.setPropietarioNombres(f.texto("propietarioNombres"));
        s.setPropietarioDni(f.texto("propietarioDni"));
        s.setPropietarioTelefono(f.texto("propietarioTelefono"));
        s.setPropietarioEmail(f.texto("propietarioEmail"));
        return s;
    }
}
