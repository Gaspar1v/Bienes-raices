package pe.bienesraices.dao;

import java.util.List;
import java.util.Optional;

import pe.bienesraices.modelo.CriterioBusqueda;
import pe.bienesraices.modelo.EstadoInmueble;
import pe.bienesraices.modelo.HistorialEstado;
import pe.bienesraices.modelo.Inmueble;
import pe.bienesraices.modelo.Propietario;

public interface InmuebleDAO {
    List<Inmueble> buscar(CriterioBusqueda criterio);

    Optional<Inmueble> buscarPorId(int idInmueble);

    int insertar(Inmueble inmueble, int idUsuario);

    void actualizar(Inmueble inmueble);

    void cambiarEstado(int idInmueble, EstadoInmueble nuevo, int idUsuario);

    List<HistorialEstado> listarHistorial(int idInmueble);

    List<Propietario> listarPropietarios();
}
