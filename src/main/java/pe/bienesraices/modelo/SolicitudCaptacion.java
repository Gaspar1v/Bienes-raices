package pe.bienesraices.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SolicitudCaptacion {
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int idSolicitud;
    private TipoOperacion operacionOfrecida;
    private TipoPropiedad tipo;
    private BigDecimal metraje;
    private String distrito;
    private String direccion;
    private BigDecimal precio;
    private Integer habitaciones;
    private Integer piso;
    private boolean cochera;
    private String acabados;
    private String referencias;
    private LocalDate fechaConstruccion;
    private String propietarioNombres;
    private String propietarioDni;
    private String propietarioTelefono;
    private String propietarioEmail;
    private EstadoSolicitud estado;
    private String observaciones;
    private String revisor;
    private LocalDateTime fechaRegistro;
    private LocalDateTime fechaRevision;
    private Integer idInmueble;

    public Inmueble aInmueble(int idPropietario) {
        Inmueble in = new Inmueble();
        in.setTipo(tipo);
        in.setOperacionOfrecida(operacionOfrecida);
        in.setMetraje(metraje);
        in.setDistrito(distrito);
        in.setDireccion(direccion);
        in.setPrecio(precio);
        in.setHabitaciones(habitaciones);
        in.setPiso(piso);
        in.setCochera(cochera);
        in.setAcabados(acabados);
        in.setReferencias(referencias);
        in.setFechaConstruccion(fechaConstruccion);
        in.setIdPropietario(idPropietario);
        return in;
    }

    public String getFechaRegistroTexto() {
        return fechaRegistro == null ? "" : fechaRegistro.format(FORMATO);
    }

    public String getFechaRevisionTexto() {
        return fechaRevision == null ? "" : fechaRevision.format(FORMATO);
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public TipoOperacion getOperacionOfrecida() { return operacionOfrecida; }
    public void setOperacionOfrecida(TipoOperacion operacionOfrecida) { this.operacionOfrecida = operacionOfrecida; }

    public TipoPropiedad getTipo() { return tipo; }
    public void setTipo(TipoPropiedad tipo) { this.tipo = tipo; }

    public BigDecimal getMetraje() { return metraje; }
    public void setMetraje(BigDecimal metraje) { this.metraje = metraje; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public Integer getHabitaciones() { return habitaciones; }
    public void setHabitaciones(Integer habitaciones) { this.habitaciones = habitaciones; }

    public Integer getPiso() { return piso; }
    public void setPiso(Integer piso) { this.piso = piso; }

    public boolean isCochera() { return cochera; }
    public void setCochera(boolean cochera) { this.cochera = cochera; }

    public String getAcabados() { return acabados; }
    public void setAcabados(String acabados) { this.acabados = acabados; }

    public String getReferencias() { return referencias; }
    public void setReferencias(String referencias) { this.referencias = referencias; }

    public LocalDate getFechaConstruccion() { return fechaConstruccion; }
    public void setFechaConstruccion(LocalDate fechaConstruccion) { this.fechaConstruccion = fechaConstruccion; }

    public String getPropietarioNombres() { return propietarioNombres; }
    public void setPropietarioNombres(String propietarioNombres) { this.propietarioNombres = propietarioNombres; }

    public String getPropietarioDni() { return propietarioDni; }
    public void setPropietarioDni(String propietarioDni) { this.propietarioDni = propietarioDni; }

    public String getPropietarioTelefono() { return propietarioTelefono; }
    public void setPropietarioTelefono(String propietarioTelefono) { this.propietarioTelefono = propietarioTelefono; }

    public String getPropietarioEmail() { return propietarioEmail; }
    public void setPropietarioEmail(String propietarioEmail) { this.propietarioEmail = propietarioEmail; }

    public EstadoSolicitud getEstado() { return estado; }
    public void setEstado(EstadoSolicitud estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getRevisor() { return revisor; }
    public void setRevisor(String revisor) { this.revisor = revisor; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public LocalDateTime getFechaRevision() { return fechaRevision; }
    public void setFechaRevision(LocalDateTime fechaRevision) { this.fechaRevision = fechaRevision; }

    public Integer getIdInmueble() { return idInmueble; }
    public void setIdInmueble(Integer idInmueble) { this.idInmueble = idInmueble; }
}
