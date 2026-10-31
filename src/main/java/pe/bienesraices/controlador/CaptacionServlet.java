package pe.bienesraices.controlador;

import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.dao.DAOException;
import pe.bienesraices.modelo.EstadoSolicitud;
import pe.bienesraices.modelo.SolicitudCaptacion;
import pe.bienesraices.servicio.ValidacionException;

@WebServlet("/app/captacion")
public class CaptacionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CaptacionServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        int id = f.id("id");
        if (id > 0) {
            Optional<SolicitudCaptacion> s = servicios().captacion().buscarPorId(id);
            if (s.isEmpty()) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("solicitud", s.get());
            vista("solicitud-detalle", req, res);
            return;
        }
        EstadoSolicitud estado = f.enumeracion("estado", EstadoSolicitud.class);
        if (estado == null && !"TODAS".equals(req.getParameter("estado"))) {
            estado = EstadoSolicitud.PENDIENTE;
        }
        req.setAttribute("filtro", estado == null ? "TODAS" : estado.name());
        req.setAttribute("estadosSolicitud", EstadoSolicitud.values());
        req.setAttribute("solicitudes", servicios().captacion().listar(estado));
        vista("solicitudes", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        int id = f.id("id");
        String volver = "/app/captacion?id=" + id;
        try {
            if ("aceptar".equals(req.getParameter("accion"))) {
                int idInmueble = servicios().captacion().aceptar(id, usuario(req));
                redirigir("/app/inmuebles?accion=ver&id=" + idInmueble,
                        "Solicitud aceptada: el inmueble se incorporó a la cartera con estado Nuevo ingreso.", req, res);
            } else if ("observar".equals(req.getParameter("accion"))) {
                servicios().captacion().observar(id, req.getParameter("observaciones"), usuario(req));
                redirigir(volver, "Observaciones registradas. La solicitud quedó en estado Observada.", req, res);
            } else {
                res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (ValidacionException e) {
            redirigir(volver, e.getErrores().values().iterator().next(), req, res);
        } catch (DAOException e) {
            LOG.error("Error al revisar la solicitud {}", id, e);
            redirigir(volver, e.getMessage(), req, res);
        }
    }
}
