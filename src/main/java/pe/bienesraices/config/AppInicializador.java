package pe.bienesraices.config;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import pe.bienesraices.dao.Conexion;

@WebListener
public class AppInicializador implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(AppInicializador.class);

    @Override
    public void contextInitialized(ServletContextEvent evento) {
        Conexion conexion = Conexion.get();

        LOG.info("Iniciando Portal Bienes Raices con base de datos {}", conexion.getUrl().split(";")[0]);
        if (Configuracion.get().activado("db.inicializar")) {
            try (Connection cn = conexion.obtener()) {
                ScriptSQL.ejecutar(cn, "/db/schema.sql");
                ScriptSQL.ejecutar(cn, "/db/datos.sql");
                LOG.info("Base de datos de demostracion creada con datos de prueba");
            } catch (Exception e) {
                LOG.error("No se pudo inicializar la base de datos", e);
                throw new IllegalStateException("Fallo la inicializacion de la base de datos", e);
            }
        }
        new Servicios(conexion).registrarEn(evento.getServletContext());
        LOG.info("Aplicacion lista");
    }
}
