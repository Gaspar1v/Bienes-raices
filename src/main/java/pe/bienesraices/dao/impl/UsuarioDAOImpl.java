package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.UsuarioDAO;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.Usuario;

public class UsuarioDAOImpl implements UsuarioDAO {
    private static final String SQL_BUSCAR =
            "SELECT id_usuario, username, password_hash, nombres, rol, activo, intentos_fallidos, bloqueado_hasta "
            + "FROM usuario WHERE username = ?";
    private static final String SQL_INTENTO =
            "UPDATE usuario SET intentos_fallidos = ?, bloqueado_hasta = ? WHERE id_usuario = ?";
    private static final String SQL_REINICIAR =
            "UPDATE usuario SET intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id_usuario = ?";
    private static final String SQL_INSERTAR =
            "INSERT INTO usuario (username, password_hash, nombres, rol, activo) VALUES (?, ?, ?, ?, ?)";

    private final FabricaConexion conexiones;

    public UsuarioDAOImpl(FabricaConexion conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public Optional<Usuario> buscarPorUsername(String username) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_BUSCAR)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setNombres(rs.getString("nombres"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                u.setActivo(rs.getBoolean("activo"));
                u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
                Timestamp bloqueo = rs.getTimestamp("bloqueado_hasta");
                u.setBloqueadoHasta(bloqueo == null ? null : bloqueo.toLocalDateTime());
                return Optional.of(u);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al buscar el usuario", e);
        }
    }

    @Override
    public void registrarIntentoFallido(int idUsuario, int intentos, LocalDateTime bloqueadoHasta) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_INTENTO)) {
            ps.setInt(1, intentos);
            ps.setTimestamp(2, bloqueadoHasta == null ? null : Timestamp.valueOf(bloqueadoHasta));
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el intento fallido", e);
        }
    }

    @Override
    public void reiniciarIntentos(int idUsuario) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_REINICIAR)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al reiniciar los intentos", e);
        }
    }

    @Override
    public int insertar(Usuario usuario) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getNombres());
            ps.setString(4, usuario.getRol().name());
            ps.setBoolean(5, usuario.isActivo());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el usuario", e);
        }
    }
}
