package pe.bienesraices.modelo;

public enum Rol {
    CLIENTE("Cliente", false),
    PROPIETARIO("Propietario", false),
    AGENTE("Agente inmobiliario", true),
    GERENTE("Gerente de agentes", true),
    ASISTENTE("Asistente administrativo", true),
    ADMINISTRADOR("Administrador del sistema", true);

    private final String etiqueta;
    private final boolean interno;

    Rol(String etiqueta, boolean interno) {
        this.etiqueta = etiqueta;
        this.interno = interno;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public boolean isInterno() {
        return interno;
    }
}
