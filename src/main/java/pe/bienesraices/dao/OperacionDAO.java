package pe.bienesraices.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.modelo.Operacion;

public interface OperacionDAO {
    int registrar(Operacion operacion, int idUsuario);

    List<Operacion> listarPorRango(LocalDate desde, LocalDate hasta);

    Optional<Operacion> buscarPorId(int idOperacion);
}
