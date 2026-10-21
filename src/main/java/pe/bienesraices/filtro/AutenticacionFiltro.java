package pe.bienesraices.filtro;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;
import pe.bienesraices.util.Auditoria;

public class AutenticacionFiltro extends HttpFilter {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession sesion = req.getSession(false);
        UsuarioSesion usuario = sesion == null ? null : (UsuarioSesion) sesion.getAttribute("usuario");
        if (usuario == null) {
            res.sendRedirect(req.getContextPath() + "/login");
            return;
        }
        String ruta = req.getServletPath();
        if (!Permisos.puedeAcceder(usuario.getRol(), ruta)) {
            Auditoria.registrar("ACCESO_DENEGADO", Auditoria.limpiar(ruta));
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "No tiene permisos para esta opcion");
            return;
        }
        chain.doFilter(req, res);
    }
}
