package pe.bienesraices.servicio;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pe.bienesraices.dao.UsuarioDAO;
import pe.bienesraices.modelo.Usuario;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.PasswordHasher;
import pe.bienesraices.util.Auditoria;

public class AuthService {
    public static final int MAX_INTENTOS = 5;
    public static final int MINUTOS_BLOQUEO = 15;

    private static final String HASH_FICTICIO = PasswordHasher.hash("no-usar".toCharArray());

    private static final Logger LOG = LoggerFactory.getLogger(AuthService.class);

    public enum Estado { EXITOSO, CREDENCIALES_INVALIDAS, CUENTA_BLOQUEADA }

    public record Resultado(Estado estado, UsuarioSesion usuario) {
    }

    private final UsuarioDAO usuarioDAO;
    private final Clock reloj;

    public AuthService(UsuarioDAO usuarioDAO, Clock reloj) {
        this.usuarioDAO = usuarioDAO;
        this.reloj = reloj;
    }

    public Resultado autenticar(String username, char[] password) {
        try {
            Optional<Usuario> encontrado = usuarioDAO.buscarPorUsername(username);
            if (encontrado.isEmpty() || !encontrado.get().isActivo()) {
                PasswordHasher.verificar(password, HASH_FICTICIO);
                Auditoria.registrar("LOGIN_FALLIDO", "usuario inexistente o inactivo: " + username);
                return new Resultado(Estado.CREDENCIALES_INVALIDAS, null);
            }
            Usuario u = encontrado.get();
            LocalDateTime ahora = LocalDateTime.now(reloj);
            if (u.estaBloqueado(ahora)) {
                Auditoria.registrar("LOGIN_BLOQUEADO", "intento sobre cuenta bloqueada: " + username);
                return new Resultado(Estado.CUENTA_BLOQUEADA, null);
            }
            if (!PasswordHasher.verificar(password, u.getPasswordHash())) {
                int intentos = u.getIntentosFallidos() + 1;
                LocalDateTime bloqueo = intentos >= MAX_INTENTOS ? ahora.plusMinutes(MINUTOS_BLOQUEO) : null;
                usuarioDAO.registrarIntentoFallido(u.getIdUsuario(), bloqueo == null ? intentos : 0, bloqueo);
                if (bloqueo != null) {
                    LOG.warn("Cuenta {} bloqueada por {} intentos fallidos", username, intentos);
                    Auditoria.registrar("CUENTA_BLOQUEADA", username);
                    return new Resultado(Estado.CUENTA_BLOQUEADA, null);
                }
                Auditoria.registrar("LOGIN_FALLIDO", "contrasena incorrecta para " + username + " intento " + intentos);
                return new Resultado(Estado.CREDENCIALES_INVALIDAS, null);
            }
            usuarioDAO.reiniciarIntentos(u.getIdUsuario());
            Auditoria.registrar("LOGIN_EXITOSO", username + " rol " + u.getRol());
            return new Resultado(Estado.EXITOSO,
                    new UsuarioSesion(u.getIdUsuario(), u.getUsername(), u.getNombres(), u.getRol()));
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
