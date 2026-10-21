package pe.bienesraices.modelo;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public record ModuloSistema(String clave, String nombre, String descripcion, String requisitos,
                            ImmutableSet<Rol> roles, boolean activo) {
    public boolean permitidoPara(Rol rol) {
        return roles.contains(rol);
    }

    public Set<Rol> getRoles() {
        return roles;
    }

    public String getClave() { return clave; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getRequisitos() { return requisitos; }
    public boolean isActivo() { return activo; }
}
