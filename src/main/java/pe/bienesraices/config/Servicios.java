package pe.bienesraices.config;

import java.time.Clock;

import jakarta.servlet.ServletContext;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.impl.UsuarioDAOImpl;
import pe.bienesraices.servicio.AuthService;

public final class Servicios {
    private static final String ATRIBUTO = Servicios.class.getName();

    private final AuthService auth;

    public Servicios(FabricaConexion conexiones) {
        this.auth = new AuthService(new UsuarioDAOImpl(conexiones), Clock.systemDefaultZone());
    }

    public void registrarEn(ServletContext contexto) {
        contexto.setAttribute(ATRIBUTO, this);
    }

    public static Servicios de(ServletContext contexto) {
        return (Servicios) contexto.getAttribute(ATRIBUTO);
    }

    public AuthService auth() {
        return auth;
    }
}
