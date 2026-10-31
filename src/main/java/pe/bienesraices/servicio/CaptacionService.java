package pe.bienesraices.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import pe.bienesraices.dao.SolicitudCaptacionDAO;
import pe.bienesraices.modelo.EstadoSolicitud;
import pe.bienesraices.modelo.SolicitudCaptacion;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.Validador;

public class CaptacionService {
    private final SolicitudCaptacionDAO dao;

    public CaptacionService(SolicitudCaptacionDAO dao) {
        this.dao = Preconditions.checkNotNull(dao, "dao");
    }

    public void validar(SolicitudCaptacion s, Validador v) {
        v.requerido("operacionOfrecida", s.getOperacionOfrecida(), "La operación")
         .requerido("tipo", s.getTipo(), "El tipo de propiedad")
         .requerido("metraje", s.getMetraje(), "El metraje")
         .positivo("metraje", s.getMetraje(), "El metraje")
         .requerido("distrito", s.getDistrito(), "El distrito")
         .longitudMaxima("distrito", s.getDistrito(), 60, "El distrito")
         .requerido("direccion", s.getDireccion(), "La dirección")
         .longitudMaxima("direccion", s.getDireccion(), 150, "La dirección")
         .requerido("precio", s.getPrecio(), "El precio solicitado")
         .positivo("precio", s.getPrecio(), "El precio solicitado")
         .rango("habitaciones", s.getHabitaciones(), 0, 50, "Las habitaciones")
         .rango("piso", s.getPiso(), -5, 80, "El piso")
         .longitudMaxima("acabados", s.getAcabados(), 200, "Los acabados")
         .longitudMaxima("referencias", s.getReferencias(), 300, "Las referencias")
         .regla(s.getFechaConstruccion() == null || !s.getFechaConstruccion().isAfter(LocalDate.now()),
                 "fechaConstruccion", "La fecha de construcción no puede ser futura.")
         .requerido("propietarioNombres", s.getPropietarioNombres(), "El nombre del propietario")
         .longitudMaxima("propietarioNombres", s.getPropietarioNombres(), 100, "El nombre")
         .requerido("propietarioDni", s.getPropietarioDni(), "El DNI")
         .dni("propietarioDni", s.getPropietarioDni())
         .requerido("propietarioTelefono", s.getPropietarioTelefono(), "El celular")
         .telefono("propietarioTelefono", s.getPropietarioTelefono())
         .requerido("propietarioEmail", s.getPropietarioEmail(), "El correo")
         .email("propietarioEmail", s.getPropietarioEmail())
         .longitudMaxima("propietarioEmail", s.getPropietarioEmail(), 120, "El correo");
        if (s.getTipo() == TipoPropiedad.TERRENO && s.getHabitaciones() != null && s.getHabitaciones() > 0) {
            v.agregar("habitaciones", "Un terreno no tiene habitaciones.");
        }
        if (s.getPrecio() != null && s.getPrecio().compareTo(new BigDecimal("100000000")) > 0) {
            v.agregar("precio", "El precio excede el máximo permitido.");
        }
    }

    public int registrar(SolicitudCaptacion s) {
        Validador v = new Validador();
        validar(s, v);
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
        int id = dao.insertar(s);
        Auditoria.registrar("CAPTACION_REGISTRADA", "id=" + id + " dni=" + Auditoria.enmascarar(s.getPropietarioDni()));
        return id;
    }

    public List<SolicitudCaptacion> listar(EstadoSolicitud estado) {
        return dao.listar(estado);
    }

    public Optional<SolicitudCaptacion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public void observar(int id, String observaciones, UsuarioSesion revisor) {
        Preconditions.checkArgument(id > 0, "Identificador de solicitud inválido: %s", id);
        Validador v = new Validador()
                .requerido("observaciones", observaciones, "Las observaciones")
                .longitudMaxima("observaciones", observaciones, 300, "Las observaciones");
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
        dao.observar(id, StringUtils.normalizeSpace(observaciones), revisor.getIdUsuario());
        Auditoria.registrar("CAPTACION_OBSERVADA", "id=" + id);
    }

    public int aceptar(int id, UsuarioSesion revisor) {
        Preconditions.checkArgument(id > 0, "Identificador de solicitud inválido: %s", id);
        int idInmueble = dao.aceptar(id, revisor.getIdUsuario());
        Auditoria.registrar("CAPTACION_ACEPTADA", "id=" + id + " inmueble=" + idInmueble);
        return idInmueble;
    }
}
