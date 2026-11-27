package pe.bienesraices.servicio;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import pe.bienesraices.dao.UsuarioDAO;
import pe.bienesraices.modelo.ResumenAgente;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.Usuario;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.PasswordHasher;
import pe.bienesraices.util.Auditoria;
import pe.bienesraices.util.Validador;

public class UsuarioService {
    static final int LONGITUD_MINIMA_CLAVE = 8;

    private final UsuarioDAO dao;

    public UsuarioService(UsuarioDAO dao) {
        this.dao = Preconditions.checkNotNull(dao, "dao");
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public List<ResumenAgente> resumenAgentes() {
        return dao.resumenAgentes();
    }

    public int crear(String username, String nombres, Rol rol, char[] clave, UsuarioSesion admin) {
        try {
            String usuario = StringUtils.lowerCase(StringUtils.trimToNull(username));
            Validador v = new Validador()
                    .requerido("username", usuario, "El usuario")
                    .regla(usuario == null || usuario.matches("[a-z0-9._]{3,30}"), "username",
                            "El usuario debe tener de 3 a 30 caracteres: letras minúsculas, números, punto o guion bajo.")
                    .requerido("nombres", StringUtils.trimToNull(nombres), "Los nombres")
                    .longitudMaxima("nombres", nombres, 100, "Los nombres")
                    .requerido("rol", rol, "El rol")
                    .regla(rol == null || rol.isInterno(), "rol", "Solo se crean cuentas para el personal interno.");
            validarClave(clave, v);
            if (usuario != null && dao.buscarPorUsername(usuario).isPresent()) {
                v.agregar("username", "Ya existe un usuario con ese nombre.");
            }
            exigir(v);
            Usuario u = new Usuario();
            u.setUsername(usuario);
            u.setNombres(StringUtils.normalizeSpace(nombres));
            u.setRol(rol);
            u.setActivo(true);
            u.setPasswordHash(PasswordHasher.hash(clave));
            int id = dao.insertar(u);
            Auditoria.registrar("USUARIO_CREADO", "id=" + id + " usuario=" + usuario + " rol=" + rol + " por " + admin.getUsername());
            return id;
        } finally {
            Arrays.fill(clave, '\0');
        }
    }

    public void actualizar(int id, String nombres, Rol rol, boolean activo, UsuarioSesion admin) {
        Usuario u = dao.buscarPorId(id).orElseThrow(() -> new ValidacionException(Map.of("id", "El usuario no existe.")));
        Validador v = new Validador()
                .requerido("nombres", StringUtils.trimToNull(nombres), "Los nombres")
                .longitudMaxima("nombres", nombres, 100, "Los nombres")
                .requerido("rol", rol, "El rol")
                .regla(rol == null || rol.isInterno(), "rol", "Solo se asignan roles del personal interno.")
                .regla(id != admin.getIdUsuario() || (activo && rol == Rol.ADMINISTRADOR), "activo",
                        "No puede desactivar su propia cuenta ni quitarse el rol de administrador.");
        exigir(v);
        u.setNombres(StringUtils.normalizeSpace(nombres));
        u.setRol(rol);
        u.setActivo(activo);
        dao.actualizar(u);
        Auditoria.registrar("USUARIO_MODIFICADO", "id=" + id + " rol=" + rol + " activo=" + activo + " por " + admin.getUsername());
    }

    public void restablecerClave(int id, char[] nueva, UsuarioSesion admin) {
        try {
            Validador v = new Validador();
            validarClave(nueva, v);
            exigir(v);
            dao.cambiarClave(id, PasswordHasher.hash(nueva));
            Auditoria.registrar("CLAVE_RESTABLECIDA", "id=" + id + " por " + admin.getUsername());
        } finally {
            Arrays.fill(nueva, '\0');
        }
    }

    private static void validarClave(char[] clave, Validador v) {
        String texto = new String(clave);
        boolean letra = texto.chars().anyMatch(Character::isLetter);
        boolean digito = texto.chars().anyMatch(Character::isDigit);
        v.regla(clave.length >= LONGITUD_MINIMA_CLAVE && letra && digito, "clave",
                "La contraseña debe tener al menos " + LONGITUD_MINIMA_CLAVE + " caracteres, con letras y números.");
    }

    private static void exigir(Validador v) {
        if (!v.esValido()) {
            throw new ValidacionException(v.getErrores());
        }
    }
}
