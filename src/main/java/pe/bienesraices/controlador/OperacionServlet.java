package pe.bienesraices.controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.dao.DAOException;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Operacion;
import pe.bienesraices.modelo.TipoOperacion;
import pe.bienesraices.servicio.ValidacionException;
import pe.bienesraices.util.Auditoria;

@WebServlet("/app/operaciones")
public class OperacionServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        int idContrato = f.id("contrato");
        if (idContrato > 0) {
            enviarContrato(idContrato, res);
            return;
        }
        Operacion op = new Operacion();
        op.setIdInmueble(f.id("idInmueble"));
        op.setFechaInicio(LocalDate.now());
        mostrar(op, null, req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        Formulario f = new Formulario(req);
        Operacion op = new Operacion();
        op.setIdInmueble(f.id("idInmueble"));
        op.setTipo(f.enumeracion("tipo", TipoOperacion.class));
        op.setClienteNombre(f.texto("clienteNombre"));
        op.setClienteDni(f.texto("clienteDni"));
        op.setFechaInicio(f.fecha("fechaInicio", "La fecha de inicio"));
        op.setFechaFin(f.fecha("fechaFin", "La fecha de fin"));
        op.setMonto(f.decimal("monto", "El monto"));
        op.setIdAgente(usuario(req).getIdUsuario());

        servicios().operaciones().validar(op, f.validador());
        if (f.validador().esValido()) {
            try {
                int id = servicios().operaciones().registrar(op, usuario(req));
                redirigir("/app/operaciones", "Operación N.° " + id + " registrada. Comisión: S/ " + op.getComision(), req, res);
                return;
            } catch (ValidacionException e) {
                e.getErrores().forEach(f.validador()::agregar);
            } catch (DAOException e) {
                f.validador().agregar("idInmueble", e.getMessage());
            }
        }
        mostrar(op, f, req, res);
    }

    private void enviarContrato(int idOperacion, HttpServletResponse res) throws IOException {
        Optional<Operacion> op = servicios().operaciones().buscarPorId(idOperacion);
        Optional<Inmueble> in = op.flatMap(o -> servicios().inmuebles().buscarPorId(o.getIdInmueble()));
        if (op.isEmpty() || in.isEmpty()) {
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        byte[] pdf = servicios().reportesPdf().contrato(op.get(), in.get());
        enviarArchivo(res, pdf, "application/pdf", "contrato_" + idOperacion + ".pdf");
        Auditoria.registrar("CONTRATO_GENERADO", "operacion=" + idOperacion);
    }

    private void mostrar(Operacion op, Formulario f, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        LocalDate hoy = LocalDate.now();
        req.setAttribute("operacion", op);
        req.setAttribute("disponibles", servicios().inmuebles().buscar(new CriterioBusqueda()));
        req.setAttribute("operaciones", servicios().operaciones().listarPorRango(hoy.withDayOfMonth(1), hoy.plusYears(1)));
        if (f != null) {
            req.setAttribute("errores", f.validador().getErrores());
        }
        vista("operaciones", req, res);
    }
}
