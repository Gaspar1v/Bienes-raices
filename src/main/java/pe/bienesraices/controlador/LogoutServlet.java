package pe.bienesraices.controlador;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bienesraices.util.Auditoria;

@WebServlet("/logout")
public class LogoutServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession sesion = req.getSession(false);
        if (sesion != null) {
            Auditoria.registrar("LOGOUT", "cierre de sesion");
            sesion.invalidate();
        }
        res.sendRedirect(req.getContextPath() + "/login");
    }
}
