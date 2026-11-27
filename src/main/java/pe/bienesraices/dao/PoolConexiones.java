package pe.bienesraices.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public final class PoolConexiones implements FabricaConexion, AutoCloseable {
    private final HikariDataSource fuente;

    public PoolConexiones(String url, String usuario, String clave, int maximo) {
        HikariConfig cfg = new HikariConfig();
        cfg.setPoolName("bienes-raices");
        cfg.setJdbcUrl(url);
        cfg.setUsername(usuario);
        cfg.setPassword(clave);
        cfg.setMaximumPoolSize(maximo);
        cfg.setMinimumIdle(Math.min(2, maximo));
        cfg.setConnectionTimeout(10_000);
        cfg.setIdleTimeout(300_000);
        cfg.setMaxLifetime(1_800_000);
        this.fuente = new HikariDataSource(cfg);
    }

    @Override
    public Connection obtener() throws SQLException {
        return fuente.getConnection();
    }

    public int activas() {
        return fuente.getHikariPoolMXBean().getActiveConnections();
    }

    public int inactivas() {
        return fuente.getHikariPoolMXBean().getIdleConnections();
    }

    public int total() {
        return fuente.getHikariPoolMXBean().getTotalConnections();
    }

    public int maximo() {
        return fuente.getMaximumPoolSize();
    }

    @Override
    public void close() {
        fuente.close();
    }
}
