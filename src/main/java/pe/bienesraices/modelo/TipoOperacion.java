package pe.bienesraices.modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public enum TipoOperacion {
    VENTA("Venta", new BigDecimal("0.03"), EstadoInmueble.VENDIDO),

    ALQUILER("Alquiler", new BigDecimal("1.00"), EstadoInmueble.ALQUILADO);

    private final String etiqueta;
    private final BigDecimal factorComision;
    private final EstadoInmueble estadoResultante;

    TipoOperacion(String etiqueta, BigDecimal factorComision, EstadoInmueble estadoResultante) {
        this.etiqueta = etiqueta;
        this.factorComision = factorComision;
        this.estadoResultante = estadoResultante;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public EstadoInmueble getEstadoResultante() {
        return estadoResultante;
    }

    public BigDecimal calcularComision(BigDecimal monto) {
        return monto.multiply(factorComision).setScale(2, RoundingMode.HALF_UP);
    }
}
