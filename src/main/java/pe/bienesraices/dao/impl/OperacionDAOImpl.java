package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.OperacionDAO;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;

public class OperacionDAOImpl implements OperacionDAO {
    private static final String SQL_INSERTAR =
            "INSERT INTO operacion (id_inmueble, tipo_operacion, cliente_nombre, cliente_dni, id_agente, "
            + "fecha_inicio, fecha_fin, monto, comision) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT =
            "SELECT o.id_operacion, o.id_inmueble, o.tipo_operacion, o.cliente_nombre, o.cliente_dni, o.id_agente, "
            + "u.nombres AS agente, p.nombres AS propietario, i.tipo, i.distrito, o.fecha_inicio, o.fecha_fin, "
            + "o.monto, o.comision, o.fecha_registro "
            + "FROM operacion o "
            + "JOIN inmueble i ON i.id_inmueble = o.id_inmueble "
            + "JOIN propietario p ON p.id_propietario = i.id_propietario "
            + "JOIN usuario u ON u.id_usuario = o.id_agente ";

    private static final String SQL_RANGO =
            SQL_SELECT + "WHERE o.fecha_inicio BETWEEN ? AND ? ORDER BY o.fecha_inicio, o.id_operacion";

    private static final String SQL_POR_ID = SQL_SELECT + "WHERE o.id_operacion = ?";

    private final FabricaConexion conexiones;

    public OperacionDAOImpl(FabricaConexion conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int registrar(Operacion op, int idUsuario) {
        try (Connection cn = conexiones.obtener()) {
            cn.setAutoCommit(false);
            try {
                EstadoInmueble actual = InmuebleDAOImpl.estadoActual(cn, op.getIdInmueble());
                if (!actual.isDisponible()) {
                    throw new DAOException("El inmueble no está disponible (estado: " + actual.getEtiqueta() + ")");
                }

                int id;
                try (PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setInt(1, op.getIdInmueble());
                    ps.setString(2, op.getTipo().name());
                    ps.setString(3, op.getClienteNombre());
                    ps.setString(4, op.getClienteDni());
                    ps.setInt(5, op.getIdAgente());
                    ps.setDate(6, Date.valueOf(op.getFechaInicio()));
                    ps.setDate(7, op.getFechaFin() == null ? null : Date.valueOf(op.getFechaFin()));
                    ps.setBigDecimal(8, op.getMonto());
                    ps.setBigDecimal(9, op.getComision());
                    ps.executeUpdate();
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        rs.next();
                        id = rs.getInt(1);
                    }
                }

                InmuebleDAOImpl.actualizarEstado(cn, op.getIdInmueble(), actual,
                        op.getTipo().getEstadoResultante(), idUsuario);
                cn.commit();
                return id;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la operación", e);
        }
    }

    @Override
    public List<Operacion> listarPorRango(LocalDate desde, LocalDate hasta) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_RANGO)) {
            ps.setDate(1, Date.valueOf(desde));
            ps.setDate(2, Date.valueOf(hasta));
            List<Operacion> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al consultar operaciones", e);
        }
    }

    @Override
    public Optional<Operacion> buscarPorId(int idOperacion) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_POR_ID)) {
            ps.setInt(1, idOperacion);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar la operación", e);
        }
    }

    private static Operacion mapear(ResultSet rs) throws SQLException {
        Operacion o = new Operacion();
        o.setIdOperacion(rs.getInt("id_operacion"));
        o.setIdInmueble(rs.getInt("id_inmueble"));
        o.setTipo(TipoOperacion.valueOf(rs.getString("tipo_operacion")));
        o.setClienteNombre(rs.getString("cliente_nombre"));
        o.setClienteDni(rs.getString("cliente_dni"));
        o.setIdAgente(rs.getInt("id_agente"));
        o.setNombreAgente(rs.getString("agente"));
        o.setNombrePropietario(rs.getString("propietario"));
        o.setDescripcionInmueble("#" + o.getIdInmueble() + " " + rs.getString("tipo") + " - " + rs.getString("distrito"));
        o.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        Date fin = rs.getDate("fecha_fin");
        o.setFechaFin(fin == null ? null : fin.toLocalDate());
        o.setMonto(rs.getBigDecimal("monto"));
        o.setComision(rs.getBigDecimal("comision"));
        o.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return o;
    }
}
