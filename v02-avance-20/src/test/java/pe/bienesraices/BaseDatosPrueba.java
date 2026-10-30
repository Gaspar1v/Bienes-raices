package pe.bienesraices;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.UUID;

import pe.bienesraices.config.ScriptSQL;
import pe.bienesraices.dao.FabricaConexion;

public final class BaseDatosPrueba {
    private BaseDatosPrueba() {
    }

    public static FabricaConexion nueva() throws Exception {
        String url = "jdbc:h2:mem:prueba_" + UUID.randomUUID()
                + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1";
        FabricaConexion fabrica = () -> DriverManager.getConnection(url, "sa", "");
        try (Connection cn = fabrica.obtener()) {
            ScriptSQL.ejecutar(cn, "/db/schema.sql");
            ScriptSQL.ejecutar(cn, "/db/datos.sql");
        }
        return fabrica;
    }
}
