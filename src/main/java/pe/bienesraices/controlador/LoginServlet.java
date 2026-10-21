package pe.bienesraices.controlador;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import pe.bienesraices.servicio.AuthService;
import pe.bienesraices.util.Auditoria;

@WebServlet("/login")
public class LoginServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        vista("login", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        String username = StringUtils.trimToEmpty(req.getParameter("username"));
        String password = StringUtils.defaultString(req.getParameter("password"));
        if (username.isEmpty() || password.isEmpty() || username.length() > 50) {
            req.setAttribute("error", "Ingrese su usuario y contrasena.");
            vista("login", req, res);
            return;
        }

        AuthService.Resultado r = servicios().auth().autenticar(username, password.toCharArray());
        switch (r.estado()) {
            case EXITOSO -> {
                req.changeSessionId();
                HttpSession sesion = req.getSession();
                sesion.setAttribute("usuario", r.usuario());
                sesion.setAttribute("inicioSesion",
                        java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                redirigir("/app/panel", "Bienvenido(a), " + r.usuario().getNombres(), req, res);
            }
            case CUENTA_BLOQUEADA -> {
                req.setAttribute("error", "Cuenta bloqueada temporalmente por intentos fallidos. Intente en "
                        + AuthService.MINUTOS_BLOQUEO + " minutos.");
                vista("login", req, res);
            }
            default -> {
                req.setAttribute("error", "Usuario o contrasena incorrectos.");
                req.setAttribute("username", Auditoria.limpiar(username));
                vista("login", req, res);
            }
        }
    }
}
