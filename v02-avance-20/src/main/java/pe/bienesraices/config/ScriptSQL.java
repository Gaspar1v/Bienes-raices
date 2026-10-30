package pe.bienesraices.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringUtils;

public final class ScriptSQL {
    private ScriptSQL() {
    }

    public static void ejecutar(Connection cn, String recurso) throws SQLException, IOException {
        String contenido;
        try (InputStream in = ScriptSQL.class.getResourceAsStream(recurso)) {
            if (in == null) {
                throw new IOException("No existe el script " + recurso);
            }
            contenido = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        StringBuilder sinComentarios = new StringBuilder();
        for (String linea : contenido.split("\\R")) {
            if (!StringUtils.startsWith(linea.trim(), "--")) {
                sinComentarios.append(linea).append('\n');
            }
        }
        try (Statement st = cn.createStatement()) {
            for (String sentencia : StringUtils.split(sinComentarios.toString(), ';')) {
                if (StringUtils.isNotBlank(sentencia)) {
                    st.execute(sentencia.trim());
                }
            }
        }
    }
}
