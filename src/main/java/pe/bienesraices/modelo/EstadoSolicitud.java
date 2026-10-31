package pe.bienesraices.modelo;

public enum EstadoSolicitud {
    PENDIENTE("Pendiente de revisión"),
    OBSERVADA("Observada"),
    ACEPTADA("Aceptada");

    private final String etiqueta;

    EstadoSolicitud(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean isRevisable() {
        return this != ACEPTADA;
    }
}
