package pe.bienesraices.seguridad;

import static pe.bienesraices.modelo.Rol.ADMINISTRADOR;
import static pe.bienesraices.modelo.Rol.AGENTE;
import static pe.bienesraices.modelo.Rol.ASISTENTE;
import static pe.bienesraices.modelo.Rol.GERENTE;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Sets;

import pe.bienesraices.modelo.ModuloSistema;
import pe.bienesraices.modelo.Rol;

public final class Permisos {
    private static final ImmutableSetMultimap<String, Rol> RUTAS = ImmutableSetMultimap.<String, Rol>builder()
            .putAll("/app/panel", AGENTE, GERENTE, ASISTENTE, ADMINISTRADOR)
            .build();

    private static final ImmutableList<ModuloSistema> MODULOS = ImmutableList.of(
            new ModuloSistema("inmuebles", "Inmuebles", "Registrar, modificar, consultar y dar de baja inmuebles; historial de estados.",
                    "RF002, RF007, RF009", Sets.immutableEnumSet(AGENTE, GERENTE, ASISTENTE, ADMINISTRADOR), false),
            new ModuloSistema("busqueda", "Búsqueda y pedidos", "Búsqueda por características y registro de pedidos de compra o alquiler.",
                    "RF003, RF004", Sets.immutableEnumSet(AGENTE, GERENTE, ADMINISTRADOR), false),
            new ModuloSistema("captacion", "Captación", "Solicitudes de propietarios, verificación de documentos e incorporación a la cartera.",
                    "RF005", Sets.immutableEnumSet(AGENTE, ASISTENTE, ADMINISTRADOR), false),
            new ModuloSistema("operaciones", "Operaciones", "Ventas y alquileres con verificación de disponibilidad y cálculo de comisiones.",
                    "RF006", Sets.immutableEnumSet(AGENTE, ASISTENTE, ADMINISTRADOR), false),
            new ModuloSistema("reportes", "Contratos y reportes", "Contratos con formato de la empresa y reportes de ventas, alquileres y comisiones.",
                    "RF008", Sets.immutableEnumSet(GERENTE, ADMINISTRADOR), false),
            new ModuloSistema("usuarios", "Usuarios y agentes", "Gestión de usuarios, roles, permisos y agentes inmobiliarios.",
                    "RF001, RF010", Sets.immutableEnumSet(ADMINISTRADOR), false));

    private Permisos() {
    }

    public static boolean puedeAcceder(Rol rol, String ruta) {
        return RUTAS.containsEntry(ruta, rol);
    }

    public static List<ModuloSistema> modulos() {
        return MODULOS;
    }
}
