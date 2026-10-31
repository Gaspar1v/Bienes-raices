package pe.bienesraices.modelo;

import java.time.LocalDateTime;

public class HistorialEstado {
    private int idHistorial;
    private int idInmueble;
    private EstadoInmueble estadoAnterior;
    private EstadoInmueble estadoNuevo;
    private LocalDateTime fecha;
    private String usuario;

    public int getIdHistorial() { return idHistorial; }
    public void setIdHistorial(int idHistorial) { this.idHistorial = idHistorial; }

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public EstadoInmueble getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(EstadoInmueble estadoAnterior) { this.estadoAnterior = estadoAnterior; }

    public EstadoInmueble getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(EstadoInmueble estadoNuevo) { this.estadoNuevo = estadoNuevo; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
}
