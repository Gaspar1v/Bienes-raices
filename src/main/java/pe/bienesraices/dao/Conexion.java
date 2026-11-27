package pe.bienesraices.dao;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.lang3.math.NumberUtils;

import pe.bienesraices.config.Configuracion;

public final class Conexion implements FabricaConexion {
    private static final Conexion INSTANCIA = new Conexion();

    private final String url;
    private final PoolConexiones pool;

    private Conexion() {
        Configuracion cfg = Configuracion.get();
        this.url = cfg.valor("db.url");
        this.pool = new PoolConexiones(url, cfg.valor("db.user"), cfg.valor("db.password"),
                NumberUtils.toInt(cfg.valor("db.pool.max"), 10));
    }

    public static Conexion get() {
        return INSTANCIA;
    }

    @Override
    public Connection obtener() throws SQLException {
        return pool.obtener();
    }

    public String getUrl() {
        return url;
    }

    public PoolConexiones getPool() {
        return pool;
    }

    public void cerrar() {
        pool.close();
    }
}
