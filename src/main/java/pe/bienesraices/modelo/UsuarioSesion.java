package pe.bienesraices.modelo;

import java.io.Serializable;

public class UsuarioSesion implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int idUsuario;
    private final String username;
    private final String nombres;
    private final Rol rol;

    public UsuarioSesion(int idUsuario, String username, String nombres, Rol rol) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.nombres = nombres;
        this.rol = rol;
    }

    public int getIdUsuario() { return idUsuario; }
    public String getUsername() { return username; }
    public String getNombres() { return nombres; }
    public Rol getRol() { return rol; }
}
