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
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;
import pe.bienesraices.servicio.InmuebleService;
import pe.bienesraices.servicio.ValidacionException;

@WebServlet("/app/inmuebles")
public class InmuebleServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(InmuebleServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        InmuebleService servicio = servicios().inmuebles();
        Formulario f = new Formulario(req);
        String accion = req.getParameter("accion");
        req.setAttribute("puedeEditar", Permisos.puedeEditarInmuebles(usuario(req).getRol()));

        if ("nuevo".equals(accion) || "editar".equals(accion)) {
            if (!Permisos.puedeEditarInmuebles(usuario(req).getRol())) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Inmueble in = "editar".equals(accion)
                    ? servicio.buscarPorId(f.id("id")).orElse(null) : new Inmueble();
            if (in == null) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            mostrarFormulario(in, null, req, res);
            return;
        }
        if ("ver".equals(accion)) {
            Optional<Inmueble> in = servicio.buscarPorId(f.id("id"));
            if (in.isEmpty()) {
                res.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            req.setAttribute("inmueble", in.get());
            req.setAttribute("historial", servicio.historial(in.get().getIdInmueble()));
            vista("inmueble-detalle", req, res);
            return;
        }

        CriterioBusqueda c = new CriterioBusqueda();
        c.setSoloDisponibles(f.casilla("soloDisponibles"));
        c.setOperacion(f.enumeracion("operacion", TipoOperacion.class));
        c.setTipo(f.enumeracion("tipo", TipoPropiedad.class));
        c.setDistrito(f.texto("distrito"));
        req.setAttribute("criterio", c);
        req.setAttribute("inmuebles", servicio.buscar(c));
        vista("inmuebles", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UsuarioSesion usuario = usuario(req);
        if (!Permisos.puedeEditarInmuebles(usuario.getRol())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Formulario f = new Formulario(req);
        if ("estado".equals(req.getParameter("accion"))) {
            int id = f.id("id");
            EstadoInmueble nuevo = f.enumeracion("estado", EstadoInmueble.class);
            if (nuevo == null) {
                redirigir("/app/inmuebles?accion=ver&id=" + id, "Seleccione un estado válido.", req, res);
                return;
            }
            try {
                servicios().inmuebles().cambiarEstado(id, nuevo, usuario);
                redirigir("/app/inmuebles?accion=ver&id=" + id, "Estado actualizado a " + nuevo.getEtiqueta() + ".", req, res);
            } catch (DAOException e) {
                LOG.error("Error al cambiar estado del inmueble {}", id, e);
                redirigir("/app/inmuebles?accion=ver&id=" + id, e.getMessage(), req, res);
            }
            return;
        }

        Inmueble in = leerInmueble(f);
        servicios().inmuebles().validar(in, f.validador());
        if (!f.validador().esValido()) {
            mostrarFormulario(in, f, req, res);
            return;
        }
        try {
            if (in.getIdInmueble() == 0) {
                int id = servicios().inmuebles().registrar(in, usuario);
                redirigir("/app/inmuebles?accion=ver&id=" + id, "Inmueble registrado con estado Nuevo ingreso.", req, res);
            } else {
                servicios().inmuebles().actualizar(in, usuario);
                redirigir("/app/inmuebles?accion=ver&id=" + in.getIdInmueble(), "Cambios guardados.", req, res);
            }
        } catch (ValidacionException e) {
            e.getErrores().forEach(f.validador()::agregar);
            mostrarFormulario(in, f, req, res);
        }
    }

    private static Inmueble leerInmueble(Formulario f) {
        Inmueble in = new Inmueble();
        in.setIdInmueble(f.id("idInmueble"));
        in.setTipo(f.enumeracion("tipo", TipoPropiedad.class));
        in.setOperacionOfrecida(f.enumeracion("operacionOfrecida", TipoOperacion.class));
        in.setMetraje(f.decimal("metraje", "El metraje"));
        in.setDistrito(f.texto("distrito"));
        in.setDireccion(f.texto("direccion"));
        in.setPrecio(f.decimal("precio", "El precio"));
        in.setHabitaciones(f.entero("habitaciones", "Las habitaciones"));
        in.setPiso(f.entero("piso", "El piso"));
        in.setCochera(f.casilla("cochera"));
        in.setAcabados(f.texto("acabados"));
        in.setReferencias(f.texto("referencias"));
        in.setFechaConstruccion(f.fecha("fechaConstruccion", "La fecha de construcción"));
        in.setIdPropietario(f.id("idPropietario"));
        return in;
    }

    private void mostrarFormulario(Inmueble in, Formulario f, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("inmueble", in);
        req.setAttribute("propietarios", servicios().inmuebles().propietarios());
        if (f != null) {
            req.setAttribute("errores", f.validador().getErrores());
        }
        vista("inmueble-form", req, res);
    }
}
