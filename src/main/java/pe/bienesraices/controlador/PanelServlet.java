package pe.bienesraices.controlador;

import java.io.IOException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.ModuloSistema;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;

@WebServlet("/app/panel")
public class PanelServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UsuarioSesion u = usuario(req);
        List<ModuloSistema> modulos = Permisos.modulos();
        long conAcceso = modulos.stream().filter(m -> m.permitidoPara(u.getRol())).count();
        req.setAttribute("modulos", modulos);
        req.setAttribute("modulosConAcceso", conAcceso);
        req.setAttribute("minutosSesion", req.getSession().getMaxInactiveInterval() / 60);
        vista("panel", req, res);
    }
}
