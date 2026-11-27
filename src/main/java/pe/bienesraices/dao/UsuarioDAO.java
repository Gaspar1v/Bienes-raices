package pe.bienesraices.dao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import pe.bienesraices.modelo.ResumenAgente;
import pe.bienesraices.modelo.Usuario;

public interface UsuarioDAO {
    Optional<Usuario> buscarPorUsername(String username);

    void registrarIntentoFallido(int idUsuario, int intentos, LocalDateTime bloqueadoHasta);

    void reiniciarIntentos(int idUsuario);

    int insertar(Usuario usuario);

    List<Usuario> listar();

    Optional<Usuario> buscarPorId(int idUsuario);

    void actualizar(Usuario usuario);

    void cambiarClave(int idUsuario, String passwordHash);

    List<ResumenAgente> resumenAgentes();
}
