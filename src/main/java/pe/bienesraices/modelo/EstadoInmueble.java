package pe.bienesraices.modelo;

public enum EstadoInmueble {
    NUEVO_INGRESO("Nuevo ingreso"),
    EN_RESERVA("En reserva"),
    VENDIDO("Vendido"),
    ALQUILADO("Alquilado"),
    DADO_DE_BAJA("Dado de baja");

    private final String etiqueta;

    EstadoInmueble(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean isDisponible() {
        return this == NUEVO_INGRESO || this == EN_RESERVA;
    }
}
