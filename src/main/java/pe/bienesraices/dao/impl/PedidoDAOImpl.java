package pe.bienesraices.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import pe.bienesraices.dao.DAOException;
import pe.bienesraices.dao.FabricaConexion;
import pe.bienesraices.dao.PedidoDAO;
import pe.bienesraices.modelo.Pedido;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.TipoPropiedad;

public class PedidoDAOImpl implements PedidoDAO {
    private static final String SQL_INSERTAR =
            "INSERT INTO pedido (tipo_operacion, tipo_propiedad, distrito, precio_maximo, metraje_minimo, "
            + "habitaciones_minimas, nombre_contacto, email, telefono, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_LISTAR =
            "SELECT id_pedido, tipo_operacion, tipo_propiedad, distrito, precio_maximo, metraje_minimo, "
            + "habitaciones_minimas, nombre_contacto, email, telefono, fecha, estado FROM pedido ORDER BY fecha DESC";

    private final FabricaConexion conexiones;

    public PedidoDAOImpl(FabricaConexion conexiones) {
        this.conexiones = conexiones;
    }

    @Override
    public int insertar(Pedido p) {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getTipoOperacion().name());
            ps.setString(2, p.getTipoPropiedad() == null ? null : p.getTipoPropiedad().name());
            ps.setString(3, p.getDistrito());
            ps.setBigDecimal(4, p.getPrecioMaximo());
            ps.setBigDecimal(5, p.getMetrajeMinimo());
            ps.setObject(6, p.getHabitacionesMinimas(), Types.INTEGER);
            ps.setString(7, p.getNombreContacto());
            ps.setString(8, p.getEmail());
            ps.setString(9, p.getTelefono());
            ps.setString(10, "PENDIENTE");
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DAOException("Error al registrar el pedido", e);
        }
    }

    @Override
    public List<Pedido> listar() {
        try (Connection cn = conexiones.obtener();
             PreparedStatement ps = cn.prepareStatement(SQL_LISTAR);
             ResultSet rs = ps.executeQuery()) {
            List<Pedido> lista = new ArrayList<>();
            while (rs.next()) {
                Pedido p = new Pedido();
                p.setIdPedido(rs.getInt("id_pedido"));
                p.setTipoOperacion(TipoOperacion.valueOf(rs.getString("tipo_operacion")));
                String tipo = rs.getString("tipo_propiedad");
                p.setTipoPropiedad(tipo == null ? null : TipoPropiedad.valueOf(tipo));
                p.setDistrito(rs.getString("distrito"));
                p.setPrecioMaximo(rs.getBigDecimal("precio_maximo"));
                p.setMetrajeMinimo(rs.getBigDecimal("metraje_minimo"));
                p.setHabitacionesMinimas(rs.getObject("habitaciones_minimas", Integer.class));
                p.setNombreContacto(rs.getString("nombre_contacto"));
                p.setEmail(rs.getString("email"));
                p.setTelefono(rs.getString("telefono"));
                p.setFecha(rs.getTimestamp("fecha").toLocalDateTime());
                p.setEstado(rs.getString("estado"));
                lista.add(p);
            }
            return lista;
        } catch (SQLException e) {
            throw new DAOException("Error al listar pedidos", e);
        }
    }
}
