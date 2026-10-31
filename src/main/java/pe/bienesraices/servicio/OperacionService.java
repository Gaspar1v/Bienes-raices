package pe.bienesraices.servicio;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.dao.OperacionDAO;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.Validador;

public class OperacionService {
    private final OperacionDAO dao;

    public OperacionService(OperacionDAO dao) {
        this.dao = dao;
    }

    public void validar(Operacion op, Validador v) {
        v.regla(op.getIdInmueble() > 0, "idInmueble", "Seleccione el inmueble.")
         .requerido("tipo", op.getTipo(), "El tipo de operación")
         .requerido("clienteNombre", op.getClienteNombre(), "El nombre del cliente")
         .longitudMaxima("clienteNombre", op.getClienteNombre(), 100, "El nombre del cliente")
         .requerido("clienteDni", op.getClienteDni(), "El DNI del cliente")
         .dni("clienteDni", op.getClienteDni())
         .requerido("fechaInicio", op.getFechaInicio(), "La fecha de inicio")
         .requerido("monto", op.getMonto(), "El monto")
         .positivo("monto", op.getMonto(), "El monto");
        if (op.getTipo() == TipoOperacion.ALQUILER) {
            v.requerido("fechaFin", op.getFechaFin(), "La fecha de fin del contrato");
            if (op.getFechaInicio() != null && op.getFechaFin() != null && !op.getFechaFin().isAfter(op.getFechaInicio())) {
                v.agregar("fechaFin", "La fecha de fin debe ser posterior a la de inicio.");
            }
        }
    }

    public int registrar(Operacion op, UsuarioSesion usuario) {
        if (op.getIdAgente() == 0) {
            op.setIdAgente(usuario.getIdUsuario());
        }
        Validador v = new Validador();
        validar(op, v);
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
        if (op.getTipo() == TipoOperacion.VENTA) {
            op.setFechaFin(null);
        }
        op.setComision(op.getTipo().calcularComision(op.getMonto()));
        int id = dao.registrar(op, usuario.getIdUsuario());
        Auditoria.registrar("OPERACION_REGISTRADA", "id=" + id + " tipo=" + op.getTipo() + " inmueble=" + op.getIdInmueble()
                + " cliente_dni=" + Auditoria.enmascarar(op.getClienteDni()) + " comision=" + op.getComision());
        return id;
    }

    public List<Operacion> listarPorRango(LocalDate desde, LocalDate hasta) {
        return dao.listarPorRango(desde, hasta);
    }

    public Optional<Operacion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }
}
