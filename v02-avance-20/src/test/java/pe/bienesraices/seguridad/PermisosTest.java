package pe.bienesraices.seguridad;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import pe.bienesraices.modelo.ModuloSistema;
import pe.bienesraices.modelo.Rol;

class PermisosTest {

    @Test
    void todosLosRolesInternosEntranAlPanel() {
        for (Rol rol : Rol.values()) {
            assertEquals(rol.isInterno(), Permisos.puedeAcceder(rol, "/app/panel"), rol.name());
        }
    }

    @Test
    void denegarPorDefectoLasRutasNoRegistradas() {
        assertFalse(Permisos.puedeAcceder(Rol.ADMINISTRADOR, "/app/cualquier-cosa"));
        assertFalse(Permisos.puedeAcceder(Rol.ADMINISTRADOR, "/app/reportes"));
    }

    @Test
    void losModulosRespetanLosRolesDelCasoDeEstudio() {
        ModuloSistema reportes = Permisos.modulos().stream().filter(m -> m.clave().equals("reportes")).findFirst().orElseThrow();
        assertTrue(reportes.permitidoPara(Rol.GERENTE));
        assertFalse(reportes.permitidoPara(Rol.AGENTE));
        assertEquals(6, Permisos.modulos().size());
        assertTrue(Permisos.modulos().stream().noneMatch(ModuloSistema::activo));
    }

    @Test
    void elMenuSoloMuestraModulosActivosYPermitidos() {
        assertTrue(Permisos.menu(Rol.AGENTE).stream().allMatch(m -> m.activo() && m.permitidoPara(Rol.AGENTE)));
        assertTrue(Permisos.menu(Rol.CLIENTE).isEmpty());
    }

    @Test
    void laListaDeModulosNoSePuedeModificar() {
        assertThrows(UnsupportedOperationException.class, () -> Permisos.modulos().clear());
    }
}
