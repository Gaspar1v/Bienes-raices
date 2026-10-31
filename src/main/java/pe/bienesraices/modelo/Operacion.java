package pe.bienesraices.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Operacion {
    private int idOperacion;
    private int idInmueble;
    private String descripcionInmueble;
    private TipoOperacion tipo;
    private String clienteNombre;
    private String clienteDni;
    private int idAgente;
    private String nombreAgente;
    private String nombrePropietario;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private BigDecimal monto;
    private BigDecimal comision;
    private LocalDateTime fechaRegistro;

    public int getIdOperacion() { return idOperacion; }
    public void setIdOperacion(int idOperacion) { this.idOperacion = idOperacion; }

    public int getIdInmueble() { return idInmueble; }
    public void setIdInmueble(int idInmueble) { this.idInmueble = idInmueble; }

    public String getDescripcionInmueble() { return descripcionInmueble; }
    public void setDescripcionInmueble(String descripcionInmueble) { this.descripcionInmueble = descripcionInmueble; }

    public TipoOperacion getTipo() { return tipo; }
    public void setTipo(TipoOperacion tipo) { this.tipo = tipo; }

    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }

    public String getClienteDni() { return clienteDni; }
    public void setClienteDni(String clienteDni) { this.clienteDni = clienteDni; }

    public int getIdAgente() { return idAgente; }
    public void setIdAgente(int idAgente) { this.idAgente = idAgente; }

    public String getNombreAgente() { return nombreAgente; }
    public void setNombreAgente(String nombreAgente) { this.nombreAgente = nombreAgente; }

    public String getNombrePropietario() { return nombrePropietario; }
    public void setNombrePropietario(String nombrePropietario) { this.nombrePropietario = nombrePropietario; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public BigDecimal getComision() { return comision; }
    public void setComision(BigDecimal comision) { this.comision = comision; }

    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}
