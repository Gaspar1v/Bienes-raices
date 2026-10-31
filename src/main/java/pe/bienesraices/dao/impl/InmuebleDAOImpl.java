package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.InmuebleDAO;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.HistorialEstado;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Propietario;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

public class InmuebleDAOImpl implements InmuebleDAO {
    private static final String SQL_SELECT =
            "SELECT i.id_inmueble, i.tipo, i.operacion_ofrecida, i.metraje, i.distrito, i.direccion, i.precio, "
            + "i.habitaciones, i.piso, i.cochera, i.acabados, i.referencias, i.fecha_construccion, "
            + "i.id_propietario, p.nombres AS propietario, i.estado, i.fecha_registro "
            + "FROM inmueble i JOIN propietario p ON p.id_propietario = i.id_propietario ";

    private static final String SQL_INSERTAR =
            "INSERT INTO inmueble (tipo, operacion_ofrecida, metraje, distrito, direccion, precio, habitaciones, "
            + "piso, cochera, acabados, referencias, fecha_construccion, id_propietario, estado) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE inmueble SET tipo = ?, operacion_ofrecida = ?, metraje = ?, distrito = ?, direccion = ?, "
            + "precio = ?, habitaciones = ?, piso = ?, cochera = ?, acabados = ?, referencias = ?, "
            + "fecha_construccion = ?, id_propietario = ? WHERE id_inmueble = ?";

    private static final String SQL_ESTADO_ACTUAL =
            "SELECT estado FROM inmueble WHERE id_inmueble = ? FOR UPDATE";
    private static final String SQL_CAMBIAR_ESTADO =
            "UPDATE inmueble SET estado = ? WHERE id_inmueble = ?";
    static final String SQL_HISTORIAL =
            "INSERT INTO historial_estado (id_inmueble, estado_anterior, estado_nuevo, id_usuario) VALUES (?, ?, ?, ?)";
    private static final String SQL_LISTAR_HISTORIAL =
            "SELECT h.id_historial, h.id_inmueble, h.estado_anterior, h.estado_nuevo, h.fecha, u.username "
            + "FROM historial_estado h JOIN usuario u ON u.id_usuario = h.id_usuario "
            + "WHERE h.id_inmueble = ? ORDER BY h.fecha DESC, h.id_historial DESC";

    private final FabricaConexion conexiones;

    public InmuebleDAOImpl(FabricaConexion conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public List<Inmueble> buscar(CriterioBusqueda c) {
        StringBuilder sql = new StringBuilder(SQL_SELECT).append("WHERE 1 = 1 ");
        List<Object> parametros = new ArrayList<>();

        if (c.isSoloDisponibles()) {
            sql.append("AND i.estado IN (?, ?) ");
            parametros.add(EstadoInmueble.NUEVO_INGRESO.name());
            parametros.add(EstadoInmueble.EN_RESERVA.name());
        }
        if (c.getOperacion() != null) {
            sql.append("AND i.operacion_ofrecida = ? ");
            parametros.add(c.getOperacion().name());
        }
        if (c.getTipo() != null) {
            sql.append("AND i.tipo = ? ");
            parametros.add(c.getTipo().name());
        }
        if (StringUtils.isNotBlank(c.getDistrito())) {
            sql.append("AND LOWER(i.distrito) LIKE ? ");
            parametros.add("%" + c.getDistrito().trim().toLowerCase(Locale.ROOT) + "%");
        }
        if (c.getPrecioMinimo() != null) {
            sql.append("AND i.precio >= ? ");
            parametros.add(c.getPrecioMinimo());
        }
        if (c.getPrecioMaximo() != null) {
            sql.append("AND i.precio <= ? ");
            parametros.add(c.getPrecioMaximo());
        }
        if (c.getMetrajeMinimo() != null) {
            sql.append("AND i.metraje >= ? ");
            parametros.add(c.getMetrajeMinimo());
        }
        if (c.getHabitacionesMinimas() != null) {
            sql.append("AND i.habitaciones >= ? ");
            parametros.add(c.getHabitacionesMinimas());
        }
        sql.append("ORDER BY i.fecha_registro DESC, i.id_inmueble DESC");

        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(sql.toString())) {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }
            List<Inmueble> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al buscar inmuebles", e);
        }
    }

    @Override
    public Optional<Inmueble> buscarPorId(int idInmueble) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_SELECT + "WHERE i.id_inmueble = ?")) {
            ps.setInt(1, idInmueble);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el inmueble", e);
        }
    }

    @Override
    public int insertar(Inmueble in, int idUsuario) {
        try (Connection cn = conexiones.obtener()) {
            cn.setAutoCommit(false);
            try {
                int id = insertarEn(cn, in, idUsuario);
                cn.commit();
                return id;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el inmueble", e);
        }
    }

    static int insertarEn(Connection cn, Inmueble in, int idUsuario) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement psHist = cn.prepareStatement(SQL_HISTORIAL)) {
            asignarCampos(ps, in);
            ps.setString(14, EstadoInmueble.NUEVO_INGRESO.name());
            ps.executeUpdate();
            int id;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                id = rs.getInt(1);
            }
            psHist.setInt(1, id);
            psHist.setNull(2, Types.VARCHAR);
            psHist.setString(3, EstadoInmueble.NUEVO_INGRESO.name());
            psHist.setInt(4, idUsuario);
            psHist.executeUpdate();
            return id;
        }
    }

    @Override
    public void actualizar(Inmueble in) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_ACTUALIZAR)) {
            asignarCampos(ps, in);
            ps.setInt(14, in.getIdInmueble());
            if (ps.executeUpdate() == 0) {
                throw new DAOException("El inmueble no existe");
            }
        } catch (SQLException e) {
            throw new DAOException("Error al actualizar el inmueble", e);
        }
    }

    @Override
    public void cambiarEstado(int idInmueble, EstadoInmueble nuevo, int idUsuario) {
        try (Connection cn = conexiones.obtener()) {
            cn.setAutoCommit(false);
            try {
                EstadoInmueble anterior = estadoActual(cn, idInmueble);
                if (anterior != nuevo) {
                    actualizarEstado(cn, idInmueble, anterior, nuevo, idUsuario);
                }
                cn.commit();
            } catch (SQLException | RuntimeException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DAOException("Error al cambiar el estado del inmueble", e);
        }
    }

    static EstadoInmueble estadoActual(Connection cn, int idInmueble) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_ESTADO_ACTUAL)) {
            ps.setInt(1, idInmueble);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DAOException("El inmueble " + idInmueble + " no existe");
                }
                return EstadoInmueble.valueOf(rs.getString(1));
            }
        }
    }

    static void actualizarEstado(Connection cn, int idInmueble, EstadoInmueble anterior,
                                 EstadoInmueble nuevo, int idUsuario) throws SQLException {
        try (PreparedStatement ps = cn.prepareStatement(SQL_CAMBIAR_ESTADO);
             PreparedStatement psHist = cn.prepareStatement(SQL_HISTORIAL)) {
            ps.setString(1, nuevo.name());
            ps.setInt(2, idInmueble);
            ps.executeUpdate();
            psHist.setInt(1, idInmueble);
            psHist.setString(2, anterior.name());
            psHist.setString(3, nuevo.name());
            psHist.setInt(4, idUsuario);
            psHist.executeUpdate();
        }
    }

    @Override
    public List<HistorialEstado> listarHistorial(int idInmueble) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR_HISTORIAL)) {
            ps.setInt(1, idInmueble);
            List<HistorialEstado> lista = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialEstado h = new HistorialEstado();
                    h.setIdHistorial(rs.getInt("id_historial"));
                    h.setIdInmueble(rs.getInt("id_inmueble"));
                    String anterior = rs.getString("estado_anterior");
                    h.setEstadoAnterior(anterior == null ? null : EstadoInmueble.valueOf(anterior));
                    h.setEstadoNuevo(EstadoInmueble.valueOf(rs.getString("estado_nuevo")));
                    h.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                    h.setUsuario(rs.getString("username"));
                    lista.add(h);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al consultar el historial", e);
        }
    }

    @Override
    public List<Propietario> listarPropietarios() {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(
                     "SELECT id_propietario, nombres, dni, telefono, email FROM propietario ORDER BY nombres");
             ResultSet rs = ps.executeQuery()) {
            List<Propietario> lista = new ArrayList<>();
            while (rs.next()) {
                Propietario p = new Propietario();
                p.setIdPropietario(rs.getInt(1));
                p.setNombres(rs.getString(2));
                p.setDni(rs.getString(3));
                p.setTelefono(rs.getString(4));
                p.setEmail(rs.getString(5));
                lista.add(p);
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al listar propietarios", e);
        }
    }

    private static void asignarCampos(PreparedStatement ps, Inmueble in) throws SQLException {
        ps.setString(1, in.getTipo().name());
        ps.setString(2, in.getOperacionOfrecida().name());
        ps.setBigDecimal(3, in.getMetraje());
        ps.setString(4, in.getDistrito());
        ps.setString(5, in.getDireccion());
        ps.setBigDecimal(6, in.getPrecio());
        ps.setObject(7, in.getHabitaciones(), Types.INTEGER);
        ps.setObject(8, in.getPiso(), Types.INTEGER);
        ps.setBoolean(9, in.isCochera());
        ps.setString(10, in.getAcabados());
        ps.setString(11, in.getReferencias());
        ps.setDate(12, in.getFechaConstruccion() == null ? null : Date.valueOf(in.getFechaConstruccion()));
        ps.setInt(13, in.getIdPropietario());
    }

    private static Inmueble mapear(ResultSet rs) throws SQLException {
        Inmueble in = new Inmueble();
        in.setIdInmueble(rs.getInt("id_inmueble"));
        in.setTipo(TipoPropiedad.valueOf(rs.getString("tipo")));
        in.setOperacionOfrecida(TipoOperacion.valueOf(rs.getString("operacion_ofrecida")));
        in.setMetraje(rs.getBigDecimal("metraje"));
        in.setDistrito(rs.getString("distrito"));
        in.setDireccion(rs.getString("direccion"));
        in.setPrecio(rs.getBigDecimal("precio"));
        in.setHabitaciones(rs.getObject("habitaciones", Integer.class));
        in.setPiso(rs.getObject("piso", Integer.class));
        in.setCochera(rs.getBoolean("cochera"));
        in.setAcabados(rs.getString("acabados"));
        in.setReferencias(rs.getString("referencias"));
        Date fc = rs.getDate("fecha_construccion");
        in.setFechaConstruccion(fc == null ? null : fc.toLocalDate());
        in.setIdPropietario(rs.getInt("id_propietario"));
        in.setNombrePropietario(rs.getString("propietario"));
        in.setEstado(EstadoInmueble.valueOf(rs.getString("estado")));
        in.setFechaRegistro(rs.getTimestamp("fecha_registro").toLocalDateTime());
        return in;
    }
}
