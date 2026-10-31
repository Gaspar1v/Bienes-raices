package pe.bienesraices.servicio;

import java.util.List;

import pe.bienesraices.dao.PedidoDAO;
import pe.bienesraices.modelo.Pedido;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.Validador;

public class PedidoService {
    private final PedidoDAO dao;

    public PedidoService(PedidoDAO dao) {
        this.dao = dao;
    }

    public void validar(Pedido p, Validador v) {
        v.requerido("tipoOperacion", p.getTipoOperacion(), "El tipo de operación")
         .requerido("nombreContacto", p.getNombreContacto(), "El nombre de contacto")
         .longitudMaxima("nombreContacto", p.getNombreContacto(), 100, "El nombre")
         .requerido("email", p.getEmail(), "El correo")
         .email("email", p.getEmail())
         .longitudMaxima("email", p.getEmail(), 120, "El correo")
         .requerido("telefono", p.getTelefono(), "El teléfono")
         .telefono("telefono", p.getTelefono())
         .longitudMaxima("distrito", p.getDistrito(), 60, "El distrito")
         .positivo("precioMaximo", p.getPrecioMaximo(), "El precio máximo")
         .positivo("metrajeMinimo", p.getMetrajeMinimo(), "El metraje mínimo")
         .rango("habitacionesMinimas", p.getHabitacionesMinimas(), 0, 50, "Las habitaciones");
    }

    public int registrar(Pedido p) {
        Validador v = new Validador();
        validar(p, v);
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
        int id = dao.insertar(p);
        Auditoria.registrar("PEDIDO_REGISTRADO", "id=" + id + " tel=" + Auditoria.enmascarar(p.getTelefono()));
        return id;
    }

    public List<Pedido> listar() {
        return dao.listar();
    }
}
