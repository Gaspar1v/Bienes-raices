package pe.bienesraices.modelo;

import java.math.BigDecimal;

public class CriterioBusqueda {
    private TipoOperacion operacion;
    private TipoPropiedad tipo;
    private String distrito;
    private BigDecimal precioMinimo;
    private BigDecimal precioMaximo;
    private BigDecimal metrajeMinimo;
    private Integer habitacionesMinimas;
    private boolean soloDisponibles = true;

    public TipoOperacion getOperacion() { return operacion; }
    public void setOperacion(TipoOperacion operacion) { this.operacion = operacion; }

    public TipoPropiedad getTipo() { return tipo; }
    public void setTipo(TipoPropiedad tipo) { this.tipo = tipo; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public BigDecimal getPrecioMinimo() { return precioMinimo; }
    public void setPrecioMinimo(BigDecimal precioMinimo) { this.precioMinimo = precioMinimo; }

    public BigDecimal getPrecioMaximo() { return precioMaximo; }
    public void setPrecioMaximo(BigDecimal precioMaximo) { this.precioMaximo = precioMaximo; }

    public BigDecimal getMetrajeMinimo() { return metrajeMinimo; }
    public void setMetrajeMinimo(BigDecimal metrajeMinimo) { this.metrajeMinimo = metrajeMinimo; }

    public Integer getHabitacionesMinimas() { return habitacionesMinimas; }
    public void setHabitacionesMinimas(Integer habitacionesMinimas) { this.habitacionesMinimas = habitacionesMinimas; }

    public boolean isSoloDisponibles() { return soloDisponibles; }
    public void setSoloDisponibles(boolean soloDisponibles) { this.soloDisponibles = soloDisponibles; }
}
