package pe.bienesraices.dao;

import java.util.List;
import java.util.Optional;

import pe.bienesraices.modelo.EstadoSolicitud;
import pe.bienesraices.modelo.SolicitudCaptacion;

public interface SolicitudCaptacionDAO {
    int insertar(SolicitudCaptacion solicitud);

    List<SolicitudCaptacion> listar(EstadoSolicitud estado);

    Optional<SolicitudCaptacion> buscarPorId(int idSolicitud);

    void observar(int idSolicitud, String observaciones, int idRevisor);

    int aceptar(int idSolicitud, int idRevisor);
}
