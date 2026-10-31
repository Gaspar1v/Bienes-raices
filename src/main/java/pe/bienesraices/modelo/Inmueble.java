package pe.bienesraices.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Inmueble {
    private int idInmueble;
    private TipoPropiedad tipo;
    private TipoOperacion operacionOfrecida;
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
    private int idPropietario;
    private String nombrePropietario;
    private EstadoInmueble estado;
    private LocalDateTime fechaRegistro;

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public TipoPropiedad getTipo() { return tipo; }
    public void setTipo(TipoPropiedad tipo) { this.tipo = tipo; }

    public TipoOperacion getOperacionOfrecida() { return operacionOfrecida; }
    public void setOperacionOfrecida(TipoOperacion operacionOfrecida) { this.operacionOfrecida = operacionOfrecida; }

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

    public int getIdPropietario() { return idPropietario; }
    public void setIdPropietario(int idPropietario) { this.idPropietario = idPropietario; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public EstadoInmueble getEstado() { return estado; }
    public void setEstado(EstadoInmueble estado) { this.estado = estado; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
