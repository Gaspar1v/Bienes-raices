package pe.bienesraices.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import pe.bienesraices.config.Configuracion;

public final class Conexion implements FabricaConexion {
    private static final Conexion INSTANCIA = new Conexion();

    private final String url;
    private final String usuario;
    private final String clave;

    private Conexion() {
        Configuracion cfg = Configuracion.get();
        this.url = cfg.valor("db.url");
        this.usuario = cfg.valor("db.user");
        this.clave = cfg.valor("db.password");
    }

    public static Conexion get() {
        return INSTANCIA;
    }

    @Override
    public Connection obtener() throws SQLException {
        return DriverManager.getConnection(url, usuario, clave);
    }

    public String getUrl() {
        return url;
    }
}
