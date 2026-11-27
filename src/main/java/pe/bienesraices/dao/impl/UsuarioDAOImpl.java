package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.UsuarioDAO;
import pe.bienesraices.modelo.ResumenAgente;
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
    private static final String SQL_LISTAR =
            "SELECT id_usuario, username, password_hash, nombres, rol, activo, intentos_fallidos, bloqueado_hasta "
            + "FROM usuario ORDER BY rol, nombres";
    private static final String SQL_POR_ID =
            "SELECT id_usuario, username, password_hash, nombres, rol, activo, intentos_fallidos, bloqueado_hasta "
            + "FROM usuario WHERE id_usuario = ?";
    private static final String SQL_ACTUALIZAR =
            "UPDATE usuario SET nombres = ?, rol = ?, activo = ? WHERE id_usuario = ?";
    private static final String SQL_CLAVE =
            "UPDATE usuario SET password_hash = ?, intentos_fallidos = 0, bloqueado_hasta = NULL WHERE id_usuario = ?";
    private static final String SQL_AGENTES =
            "SELECT u.id_usuario, u.nombres, u.username, u.activo, COUNT(o.id_operacion) AS operaciones, "
            + "COALESCE(SUM(CASE WHEN o.tipo_operacion = 'VENTA' THEN 1 ELSE 0 END), 0) AS ventas, "
            + "COALESCE(SUM(CASE WHEN o.tipo_operacion = 'ALQUILER' THEN 1 ELSE 0 END), 0) AS alquileres, "
            + "COALESCE(SUM(o.comision), 0) AS comision "
            + "FROM usuario u LEFT JOIN operacion o ON o.id_agente = u.id_usuario "
            + "WHERE u.rol = 'AGENTE' GROUP BY u.id_usuario, u.nombres, u.username, u.activo "
            + "ORDER BY comision DESC, u.nombres";

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
                return Optional.of(mapear(rs));
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

    @Override
    public List<Usuario> listar() {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            List<Usuario> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al listar los usuarios", e);
        }
    }

    @Override
    public Optional<Usuario> buscarPorId(int idUsuario) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el usuario", e);
        }
    }

    @Override
    public void actualizar(Usuario u) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setString(1, u.getNombres());
            ps.setString(2, u.getRol().name());
            ps.setBoolean(3, u.isActivo());
            ps.setInt(4, u.getIdUsuario());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("El usuario no existe");
            }
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el usuario", e);
        }
    }

    @Override
    public void cambiarClave(int idUsuario, String passwordHash) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_CLAVE)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, idUsuario);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error al cambiar la contraseña", e);
        }
    }

    @Override
    public List<ResumenAgente> resumenAgentes() {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_AGENTES);
             ResultSet rs = ps.executeQuery()) {
            List<ResumenAgente> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(new ResumenAgente(rs.getInt("id_usuario"), rs.getString("nombres"), rs.getString("username"),
                        rs.getBoolean("activo"), rs.getInt("operaciones"), rs.getInt("ventas"), rs.getInt("alquileres"),
                        rs.getBigDecimal("comision")));
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el desempeño de los agentes", e);
        }
    }

    private static Usuario mapear(ResultSet rs) throws SQLException {
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
        return u;
    }
}
