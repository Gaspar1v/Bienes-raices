package pe.bienesraices.dao;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface FabricaConexion {
    Connection obtener() throws SQLException;
}
