package pe.bienesraices.filtro;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.seguridad.CsrfUtil;
import pe.bienesraices.util.Auditoria;

public class CsrfFiltro extends HttpFilter {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(CsrfFiltro.class);

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if ("POST".equalsIgnoreCase(req.getMethod())
                && !CsrfUtil.esValido(req.getSession(false), req.getParameter(CsrfUtil.PARAMETRO))) {
            LOG.warn("Peticion POST rechazada por token CSRF invalido en {}", Auditoria.limpiar(req.getRequestURI()));
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Solicitud no valida");
            return;
        }
        req.setAttribute("csrf", CsrfUtil.token(req.getSession(true)));
        chain.doFilter(req, res);
    }
}
