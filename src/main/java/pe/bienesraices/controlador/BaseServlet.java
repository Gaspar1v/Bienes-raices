package pe.bienesraices.controlador;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bienesraices.config.Servicios;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;

abstract class BaseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected Servicios servicios() {
        return Servicios.de(getServletContext());
    }

    protected UsuarioSesion usuario(HttpServletRequest req) {
        return (UsuarioSesion) req.getSession().getAttribute("usuario");
    }

    protected void vista(String nombre, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        HttpSession sesion = req.getSession(false);
        UsuarioSesion u = sesion == null ? null : (UsuarioSesion) sesion.getAttribute("usuario");
        if (u != null) {
            req.setAttribute("menu", Permisos.menu(u.getRol()));
        }
        req.setAttribute("tiposPropiedad", TipoPropiedad.values());
        req.setAttribute("tiposOperacion", TipoOperacion.values());
        req.setAttribute("estados", EstadoInmueble.values());
        req.getRequestDispatcher("/WEB-INF/vistas/" + nombre + ".jsp").forward(req, res);
    }

    protected void redirigir(String ruta, String mensaje, HttpServletRequest req, HttpServletResponse res)
            throws IOException {
        if (mensaje != null) {
            req.getSession().setAttribute("flash", mensaje);
        }
        res.sendRedirect(req.getContextPath() + ruta);
    }

    protected void enviarArchivo(HttpServletResponse res, byte[] contenido, String tipo, String nombre)
            throws IOException {
        res.setContentType(tipo);
        res.setHeader("Content-Disposition", "attachment; filename=\"" + nombre + "\"");
        res.setContentLength(contenido.length);
        res.getOutputStream().write(contenido);
    }
}
