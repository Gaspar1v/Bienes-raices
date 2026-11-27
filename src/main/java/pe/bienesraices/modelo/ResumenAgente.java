package pe.bienesraices.modelo;

import java.math.BigDecimal;

public record ResumenAgente(int idUsuario, String nombres, String username, boolean activo,
                            int operaciones, int ventas, int alquileres, BigDecimal comisionTotal) {

    public String getNombres() { return nombres; }
    public String getUsername() { return username; }
    public boolean isActivo() { return activo; }
    public int getOperaciones() { return operaciones; }
    public int getVentas() { return ventas; }
    public int getAlquileres() { return alquileres; }
    public BigDecimal getComisionTotal() { return comisionTotal; }
}
