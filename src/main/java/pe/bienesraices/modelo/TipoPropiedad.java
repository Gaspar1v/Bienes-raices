package pe.bienesraices.modelo;

public enum TipoPropiedad {
    CASA("Casa"),
    DEPARTAMENTO("Departamento"),
    LOCAL_COMERCIAL("Local comercial"),
    OFICINA("Oficina"),
    TERRENO("Terreno");

    private final String etiqueta;

    TipoPropiedad(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }
}
