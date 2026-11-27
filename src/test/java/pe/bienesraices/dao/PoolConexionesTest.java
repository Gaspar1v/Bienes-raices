package pe.bienesraices.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.UUID;

import org.junit.jupiter.api.Test;

class PoolConexionesTest {

    private static PoolConexiones nuevo(int maximo) {
        return new PoolConexiones("jdbc:h2:mem:pool_" + UUID.randomUUID() + ";DB_CLOSE_DELAY=-1", "sa", "", maximo);
    }

    @Test
    void entregaConexionesQueVuelvenAlPoolAlCerrarse() throws Exception {
        try (PoolConexiones pool = nuevo(3)) {
            try (Connection a = pool.obtener();
                 Connection b = pool.obtener();
                 Statement st = a.createStatement();
                 ResultSet rs = st.executeQuery("SELECT 1")) {
                assertTrue(rs.next());
                assertEquals(2, pool.activas());
            }
            assertEquals(0, pool.activas());
            assertTrue(pool.inactivas() >= 2);
        }
    }

    @Test
    void reutilizaLasConexionesSinSuperarElMaximo() throws Exception {
        try (PoolConexiones pool = nuevo(3)) {
            for (int i = 0; i < 50; i++) {
                try (Connection cn = pool.obtener()) {
                    assertTrue(cn.isValid(1));
                }
            }
            assertEquals(3, pool.maximo());
            assertTrue(pool.total() <= 3);
        }
    }
}
