package pe.bienesraices.servicio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.google.common.base.Suppliers;

import pe.bienesraices.dao.InmuebleDAO;
import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.HistorialEstado;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Propietario;
import pe.bienesraices.modelo.TipoPropiedad;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.Validador;

public class InmuebleService {
    private static final Logger LOG = LoggerFactory.getLogger(InmuebleService.class);

    static final long LIMITE_BUSQUEDA_MS = 5_000;

    private final InmuebleDAO dao;
    private final Supplier<List<Propietario>> propietariosEnCache;

    public InmuebleService(InmuebleDAO dao) {
        this.dao = Preconditions.checkNotNull(dao, "dao");

        this.propietariosEnCache = Suppliers.memoizeWithExpiration(dao::listarPropietarios, 5, TimeUnit.MINUTES);
    }

    public void validar(Inmueble in, Validador v) {
        v.requerido("tipo", in.getTipo(), "El tipo de propiedad")
         .requerido("operacionOfrecida", in.getOperacionOfrecida(), "La operación ofrecida")
         .requerido("metraje", in.getMetraje(), "El metraje")
         .positivo("metraje", in.getMetraje(), "El metraje")
         .requerido("distrito", in.getDistrito(), "El distrito")
         .longitudMaxima("distrito", in.getDistrito(), 60, "El distrito")
         .requerido("direccion", in.getDireccion(), "La dirección")
         .longitudMaxima("direccion", in.getDireccion(), 150, "La dirección")
         .requerido("precio", in.getPrecio(), "El precio")
         .positivo("precio", in.getPrecio(), "El precio")
         .rango("habitaciones", in.getHabitaciones(), 0, 50, "Las habitaciones")
         .rango("piso", in.getPiso(), -5, 80, "El piso")
         .longitudMaxima("acabados", in.getAcabados(), 200, "Los acabados")
         .longitudMaxima("referencias", in.getReferencias(), 300, "Las referencias")
         .regla(in.getIdPropietario() > 0, "idPropietario", "Seleccione el propietario.")
         .regla(in.getFechaConstruccion() == null || !in.getFechaConstruccion().isAfter(LocalDate.now()),
                 "fechaConstruccion", "La fecha de construcción no puede ser futura.");
        if (in.getTipo() == TipoPropiedad.TERRENO && in.getHabitaciones() != null && in.getHabitaciones() > 0) {
            v.agregar("habitaciones", "Un terreno no tiene habitaciones.");
        }
        if (in.getPrecio() != null && in.getPrecio().compareTo(new BigDecimal("100000000")) > 0) {
            v.agregar("precio", "El precio excede el máximo permitido.");
        }
    }

    public int registrar(Inmueble in, UsuarioSesion usuario) {
        Preconditions.checkNotNull(usuario, "Se requiere un usuario autenticado");
        exigirValido(in);
        int id = dao.insertar(in, usuario.getIdUsuario());
        LOG.debug("Inmueble {} registrado", id);
        Auditoria.registrar("INMUEBLE_REGISTRADO", "id=" + id + " tipo=" + in.getTipo() + " distrito=" + in.getDistrito());
        return id;
    }

    public void actualizar(Inmueble in, UsuarioSesion usuario) {
        exigirValido(in);
        dao.actualizar(in);
        Auditoria.registrar("INMUEBLE_MODIFICADO", "id=" + in.getIdInmueble() + " por " + usuario.getUsername());
    }

    public void cambiarEstado(int idInmueble, EstadoInmueble nuevo, UsuarioSesion usuario) {
        Preconditions.checkArgument(idInmueble > 0, "Identificador de inmueble inválido: %s", idInmueble);
        Preconditions.checkNotNull(nuevo, "El nuevo estado es obligatorio");
        dao.cambiarEstado(idInmueble, nuevo, usuario.getIdUsuario());
        Auditoria.registrar("ESTADO_CAMBIADO", "inmueble=" + idInmueble + " nuevo=" + nuevo);
    }

    public List<Inmueble> buscar(CriterioBusqueda criterio) {
        Stopwatch reloj = Stopwatch.createStarted();
        List<Inmueble> resultado = dao.buscar(criterio);
        long ms = reloj.elapsed(TimeUnit.MILLISECONDS);
        if (ms > LIMITE_BUSQUEDA_MS) {
            LOG.warn("Búsqueda lenta: {} ms para {} resultados (RNF002)", ms, resultado.size());
        } else {
            LOG.debug("Búsqueda de inmuebles en {} ms ({} resultados)", ms, resultado.size());
        }
        return resultado;
    }

    public Optional<Inmueble> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<HistorialEstado> historial(int id) {
        return dao.listarHistorial(id);
    }

    public List<Propietario> propietarios() {
        return propietariosEnCache.get();
    }

    private void exigirValido(Inmueble in) {
        Validador v = new Validador();
        validar(in, v);
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
    }
}
