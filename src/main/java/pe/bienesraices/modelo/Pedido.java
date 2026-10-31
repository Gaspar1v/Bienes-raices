package pe.bienesraices.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Pedido {
    private int idPedido;
    private TipoOperacion tipoOperacion;
    private TipoPropiedad tipoPropiedad;
    private String distrito;
    private BigDecimal precioMaximo;
    private BigDecimal metrajeMinimo;
    private Integer habitacionesMinimas;
    private String nombreContacto;
    private String email;
    private String telefono;
    private LocalDateTime fecha;
    private String estado;

    public int getIdPedido() { return idPedido; }
    public void setIdPedido(int idPedido) { this.idPedido = idPedido; }

    public TipoOperacion getTipoOperacion() { return tipoOperacion; }
    public void setTipoOperacion(TipoOperacion tipoOperacion) { this.tipoOperacion = tipoOperacion; }

    public TipoPropiedad getTipoPropiedad() { return tipoPropiedad; }
    public void setTipoPropiedad(TipoPropiedad tipoPropiedad) { this.tipoPropiedad = tipoPropiedad; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public BigDecimal getPrecioMaximo() { return precioMaximo; }
    public void setPrecioMaximo(BigDecimal precioMaximo) { this.precioMaximo = precioMaximo; }

    public BigDecimal getMetrajeMinimo() { return metrajeMinimo; }
    public void setMetrajeMinimo(BigDecimal metrajeMinimo) { this.metrajeMinimo = metrajeMinimo; }

    public Integer getHabitacionesMinimas() { return habitacionesMinimas; }
    public void setHabitacionesMinimas(Integer habitacionesMinimas) { this.habitacionesMinimas = habitacionesMinimas; }

    public String getNombreContacto() { return nombreContacto; }
    public void setNombreContacto(String nombreContacto) { this.nombreContacto = nombreContacto; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getFechaTexto() {
        return fecha == null ? "" : fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
