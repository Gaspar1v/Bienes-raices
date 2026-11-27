package pe.bienesraices.controlador;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pe.bienesraices.modelo.Rol;
import pe.bienesraices.modelo.Usuario;
import pe.bienesraices.modelo.UsuarioSesion;
import pe.bienesraices.seguridad.Permisos;
import pe.bienesraices.servicio.ValidacionException;

@WebServlet("/app/usuarios")
public class UsuarioServlet extends BaseServlet {
    private static final long serialVersionUID = 1L;
    private static final Rol[] ROLES_INTERNOS = Arrays.stream(Rol.values()).filter(Rol::isInterno).toArray(Rol[]::new);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UsuarioSesion u = usuario(req);
        boolean admin = Permisos.puedeAdministrarUsuarios(u.getRol());
        String accion = req.getParameter("accion");
        if ("nuevo".equals(accion) || "editar".equals(accion)) {
            if (!admin) {
                res.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            Usuario edicion = new Usuario();
            if ("editar".equals(accion)) {
                Optional<Usuario> encontrado = servicios().usuarios().buscarPorId(new Formulario(req).id("id"));
                if (encontrado.isEmpty()) {
                    res.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                edicion = encontrado.get();
            } else {
                edicion.setActivo(true);
            }
            mostrarFormulario(edicion, Map.of(), req, res);
            return;
        }
        req.setAttribute("puedeAdministrar", admin);
        req.setAttribute("usuarios", servicios().usuarios().listar());
        req.setAttribute("agentes", servicios().usuarios().resumenAgentes());
        vista("usuarios", req, res);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        UsuarioSesion admin = usuario(req);
        if (!Permisos.puedeAdministrarUsuarios(admin.getRol())) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        Formulario f = new Formulario(req);
        int id = f.id("id");
        Rol rol = f.enumeracion("rol", Rol.class);
        String nombres = f.texto("nombres");
        char[] clave = StringUtils.defaultString(req.getParameter("clave")).toCharArray();
        try {
            switch (StringUtils.defaultString(req.getParameter("accion"))) {
                case "crear" -> {
                    servicios().usuarios().crear(f.texto("username"), nombres, rol, clave, admin);
                    redirigir("/app/usuarios", "Usuario creado correctamente.", req, res);
                }
                case "actualizar" -> {
                    servicios().usuarios().actualizar(id, nombres, rol, f.casilla("activo"), admin);
                    redirigir("/app/usuarios", "Cambios guardados.", req, res);
                }
                case "clave" -> {
                    servicios().usuarios().restablecerClave(id, clave, admin);
                    redirigir("/app/usuarios", "Contraseña restablecida.", req, res);
                }
                default -> res.sendError(HttpServletResponse.SC_BAD_REQUEST);
            }
        } catch (ValidacionException e) {
            Usuario edicion = id > 0 ? servicios().usuarios().buscarPorId(id).orElse(new Usuario()) : new Usuario();
            if (id == 0) {
                edicion.setUsername(f.texto("username"));
            }
            edicion.setNombres(nombres);
            edicion.setRol(rol);
            edicion.setActivo(id == 0 || f.casilla("activo"));
            mostrarFormulario(edicion, e.getErrores(), req, res);
        }
    }

    private void mostrarFormulario(Usuario edicion, Map<String, String> errores, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        req.setAttribute("edicion", edicion);
        req.setAttribute("roles", ROLES_INTERNOS);
        req.setAttribute("errores", errores);
        vista("usuario-form", req, res);
    }
}
