package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.SolicitudCaptacionDAO;
import pe.bienesraices.modelo.EstadoSolicitud;
import pe.bienesraices.modelo.SolicitudCaptacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

public class SolicitudCaptacionDAOImpl implements SolicitudCaptacionDAO {
    private static final String SQL_INSERTAR =
            "INSERT INTO solicitud_captacion (operacion_ofrecida, tipo, metraje, distrito, direccion, precio, habitaciones, "
            + "piso, cochera, acabados, referencias, fecha_construccion, propietario_nombres, propietario_dni, "
            + "propietario_telefono, propietario_email, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT =
            "SELECT s.*, u.username AS revisor FROM solicitud_captacion s "
            + "LEFT JOIN usuario u ON u.id_usuario = s.id_revisor ";

    private static final String SQL_BLOQUEAR =
            "SELECT * FROM solicitud_captacion WHERE id_solicitud = ? FOR UPDATE";

    private static final String SQL_OBSERVAR =
            "UPDATE solicitud_captacion SET estado = 'OBSERVADA', observaciones = ?, id_revisor = ?, "
            + "fecha_revision = CURRENT_TIMESTAMP WHERE id_solicitud = ? AND estado <> 'ACEPTADA'";

    private static final String SQL_ACEPTAR =
            "UPDATE solicitud_captacion SET estado = 'ACEPTADA', id_inmueble = ?, id_revisor = ?, "
            + "fecha_revision = CURRENT_TIMESTAMP WHERE id_solicitud = ?";

    private static final String SQL_PROPIETARIO_POR_DNI =
            "SELECT id_propietario FROM propietario WHERE dni = ?";

    private static final String SQL_INSERTAR_PROPIETARIO =
            "INSERT INTO propietario (nombres, dni, telefono, email) VALUES (?, ?, ?, ?)";

    private final FabricaConexion conexiones;

    public SolicitudCaptacionDAOImpl(FabricaConexion conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(SolicitudCaptacion s) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getOperacionOfrecida().name());
            ps.setString(2, s.getTipo().name());
            ps.setBigDecimal(3, s.getMetraje());
            ps.setString(4, s.getDistrito());
            ps.setString(5, s.getDireccion());
            ps.setBigDecimal(6, s.getPrecio());
            ps.setObject(7, s.getHabitaciones(), Types.INTEGER);
            ps.setObject(8, s.getPiso(), Types.INTEGER);
            ps.setBoolean(9, s.isCochera());
            ps.setString(10, s.getAcabados());
            ps.setString(11, s.getReferencias());
            ps.setDate(12, s.getFechaConstruccion() == null ? null : Date.valueOf(s.getFechaConstruccion()));
            ps.setString(13, s.getPropietarioNombres());
            ps.setString(14, s.getPropietarioDni());
            ps.setString(15, s.getPropietarioTelefono());
            ps.setString(16, s.getPropietarioEmail());
            ps.setString(17, EstadoSolicitud.PENDIENTE.name());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar la solicitud de captación", e);
        }
    }

    @Override
    public List<SolicitudCaptacion> listar(EstadoSolicitud estado) {
        String sql = SQL_SELECT + (estado == null ? "" : "WHERE s.estado = ? ") + "ORDER BY s.fecha_registro DESC, s.id_solicitud DESC";
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (estado != null) {
                ps.setString(1, estado.name());
            }
            List<SolicitudCaptacion> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al listar las solicitudes de captación", e);
        }
    }

    @Override
    public Optional<SolicitudCaptacion> buscarPorId(int idSolicitud) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT + "WHERE s.id_solicitud = ?")) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar la solicitud de captación", e);
        }
    }

    @Override
    public void observar(int idSolicitud, String observaciones, int idRevisor) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_OBSERVAR)) {
            ps.setString(1, observaciones);
            ps.setInt(2, idRevisor);
            ps.setInt(3, idSolicitud);
            if (ps.executeUpdate() == 0) {
                throw new DAOException("La solicitud no existe o ya fue aceptada");
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar las observaciones", e);
        }
    }

    @Override
    public int aceptar(int idSolicitud, int idRevisor) {
        try (Connection cn = conexiones.obtener()) {
            cn.setAutoCommit(false);
            try {
                SolicitudCaptacion s = bloquear(cn, idSolicitud);
                if (!s.getEstado().isRevisable()) {
                    throw new DAOException("La solicitud ya fue aceptada");
                }
                int idPropietario = propietarioPorDni(cn, s);
                int idInmueble = InmuebleDAOImpl.insertarEn(cn, s.aInmueble(idPropietario), idRevisor);
                try (PreparedStatement ps = cn.prepareStatement(SQL_ACEPTAR)) {
                    ps.setInt(1, idInmueble);
                    ps.setInt(2, idRevisor);
                    ps.setInt(3, idSolicitud);
                    ps.executeUpdate();
                }
                cn.commit();
                return idInmueble;
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al incorporar el inmueble a la cartera", e);
        }
    }

    private static SolicitudCaptacion bloquear(Connection cn, int idSolicitud) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_BLOQUEAR)) {
            ps.setInt(1, idSolicitud);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DAOException("La solicitud " + idSolicitud + " no existe");
                }
                return mapearSinRevisor(rs);
            }
        }
    }

    private static int propietarioPorDni(Connection cn, SolicitudCaptacion s) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_PROPIETARIO_POR_DNI)) {
            ps.setString(1, s.getPropietarioDni());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        try (PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR_PROPIETARIO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, s.getPropietarioNombres());
            ps.setString(2, s.getPropietarioDni());
            ps.setString(3, s.getPropietarioTelefono());
            ps.setString(4, s.getPropietarioEmail());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    private static SolicitudCaptacion mapear(ResultSet rs) throws SQLException {
        SolicitudCaptacion s = mapearSinRevisor(rs);
        s.setRevisor(rs.getString("revisor"));
        return s;
    }

    private static SolicitudCaptacion mapearSinRevisor(ResultSet rs) throws SQLException {
        SolicitudCaptacion s = new SolicitudCaptacion();
        s.setIdSolicitud(rs.getInt("id_solicitud"));
        s.setOperacionOfrecida(TipoOperacion.valueOf(rs.getString("operacion_ofrecida")));
        s.setTipo(TipoPropiedad.valueOf(rs.getString("tipo")));
        s.setMetraje(rs.getBigDecimal("metraje"));
        s.setDistrito(rs.getString("distrito"));
        s.setDireccion(rs.getString("direccion"));
        s.setPrecio(rs.getBigDecimal("precio"));
        s.setHabitaciones(rs.getObject("habitaciones", Integer.class));
        s.setPiso(rs.getObject("piso", Integer.class));
        s.setCochera(rs.getBoolean("cochera"));
        s.setAcabados(rs.getString("acabados"));
        s.setReferencias(rs.getString("referencias"));
        Date fc = rs.getDate("fecha_construccion");
        s.setFechaConstruccion(fc == null ? null : fc.toLocalDate());
        s.setPropietarioNombres(rs.getString("propietario_nombres"));
        s.setPropietarioDni(rs.getString("propietario_dni"));
        s.setPropietarioTelefono(rs.getString("propietario_telefono"));
        s.setPropietarioEmail(rs.getString("propietario_email"));
        s.setEstado(EstadoSolicitud.valueOf(rs.getString("estado")));
        s.setObservaciones(rs.getString("observaciones"));
        s.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        Timestamp revision = rs.getTimestamp("fecha_revision");
        s.setFechaRevision(revision == null ? null : revision.toLocalDateTime());
        s.setIdInmueble(rs.getObject("id_inmueble", Integer.class));
        return s;
    }
}
