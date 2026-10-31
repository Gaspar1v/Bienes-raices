package pe.bienesraices.modelo;

import com.google.common.collect.ImmutableSet;

public record ModuloSistema(String clave, String nombre, String descripcion, String requisitos, String ruta,
                            boolean activo, ImmutableSet<Rol> roles) {

    public boolean permitidoPara(Rol rol) {
        return roles.contains(rol);
    }

    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getRequisitos() { return requisitos; }
    public String getRuta() { return ruta; }
    public boolean isActivo() { return activo; }
}
