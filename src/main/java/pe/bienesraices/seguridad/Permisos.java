package pe.bienesraices.seguridad;

import static pe.bienesraices.modelo.Rol.ADMINISTRADOR;
import static pe.bienesraices.modelo.Rol.AGENTE;
import static pe.bienesraices.modelo.Rol.ASISTENTE;
import static pe.bienesraices.modelo.Rol.GERENTE;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;

import pe.bienesraices.modelo.ModuloSistema;
import pe.bienesraices.modelo.Rol;

public final class Permisos {
    private static final ImmutableList<ModuloSistema> MODULOS = ImmutableList.of(
            modulo("inmuebles", "Inmuebles", "Registrar, modificar, consultar y dar de baja inmuebles; historial de estados.",
                    "RF002, RF007, RF009", "/app/inmuebles", true, AGENTE, GERENTE, ASISTENTE, ADMINISTRADOR),
            modulo("busqueda", "Búsqueda y pedidos", "Búsqueda por características y registro de pedidos de compra o alquiler.",
                    "RF003, RF004", "/app/pedidos", true, AGENTE, GERENTE, ADMINISTRADOR),
            modulo("captacion", "Captación", "Solicitudes de propietarios, verificación de documentos e incorporación a la cartera.",
                    "RF005", "/app/captacion", true, AGENTE, ASISTENTE, ADMINISTRADOR),
            modulo("operaciones", "Operaciones", "Ventas y alquileres con verificación de disponibilidad, comisiones y contratos.",
                    "RF006", "/app/operaciones", true, AGENTE, ASISTENTE, ADMINISTRADOR),
            modulo("reportes", "Contratos y reportes", "Reportes de ventas, alquileres, comisiones, clientes y empleados en Excel y PDF.",
                    "RF008", "/app/reportes", true, GERENTE, ADMINISTRADOR),
            modulo("usuarios", "Usuarios y agentes", "Gestión de usuarios, roles y permisos; desempeño de los agentes inmobiliarios.",
                    "RF001, RF010", "/app/usuarios", false, GERENTE, ADMINISTRADOR));

    private static final ImmutableSetMultimap<String, Rol> RUTAS = construirRutas();

    private static final ImmutableSet<Rol> EDITAN_INMUEBLES = Sets.immutableEnumSet(ASISTENTE, ADMINISTRADOR);

    private Permisos() {
    }

    private static ModuloSistema modulo(String clave, String nombre, String descripcion, String requisitos,
                                        String ruta, boolean activo, Rol primero, Rol... resto) {
        return new ModuloSistema(clave, nombre, descripcion, requisitos, ruta, activo, Sets.immutableEnumSet(primero, resto));
    }

    private static ImmutableSetMultimap<String, Rol> construirRutas() {
        ImmutableSetMultimap.Builder<String, Rol> rutas = ImmutableSetMultimap.builder();
        rutas.putAll("/app/panel", AGENTE, GERENTE, ASISTENTE, ADMINISTRADOR);
        for (ModuloSistema m : MODULOS) {
            if (m.activo()) {
                rutas.putAll(m.ruta(), m.roles());
            }
        }
        return rutas.build();
    }

    public static boolean puedeAcceder(Rol rol, String ruta) {
        return RUTAS.containsEntry(ruta, rol);
    }

    public static boolean puedeEditarInmuebles(Rol rol) {
        return EDITAN_INMUEBLES.contains(rol);
    }

    public static List<ModuloSistema> modulos() {
        return MODULOS;
    }

    public static List<ModuloSistema> menu(Rol rol) {
        return MODULOS.stream().filter(m -> m.activo() && m.permitidoPara(rol)).toList();
    }
}
