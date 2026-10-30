package pe.bienesraices.filtro;

import java.io.IOException;

import org.slf4j.MDC;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bienesraices.modelo.UsuarioSesion;

public class MdcFiltro extends HttpFilter {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession sesion = req.getSession(false);
        UsuarioSesion u = sesion == null ? null : (UsuarioSesion) sesion.getAttribute("usuario");
        MDC.put("usuario", u == null ? "anonimo" : u.getUsername());
        MDC.put("ip", req.getRemoteAddr());
        try {
            chain.doFilter(req, res);
        } finally {
            MDC.clear();
        }
    }
}
