# Portal Inmobiliaria Bienes Raíces

Aplicación Java para gestionar inmuebles, pedidos, captación,
operaciones y reportes en Excel y PDF.

## Requisitos
- JDK 17.
- Maven.
- Navegador web.

## Ejecutar la demostración
```bash
git clone https://github.com/Gaspar1v/Bienes-raices.git
cd Bienes-raices
mvn clean test
mvn jetty:run
```

Abrir http://localhost:8080/inmobiliaria/inicio.
Para detener el servidor, presionar Ctrl+C.

La demostración utiliza H2 en memoria: los datos se pierden
al terminar el proceso. No necesita instalar MySQL.

## Pruebas y entrega
```bash
mvn test
mvn clean package
```

El empaquetado genera:
- `target/inmobiliaria.war`
- `target/inmobiliaria-0.8.0-entrega.zip`

Los resultados de pruebas quedan en `target/surefire-reports/`.

## Configuración
La configuración predeterminada está en
`src/main/resources/config.properties`.

Se puede sustituir mediante estas variables de entorno:
- `BR_DB_URL`
- `BR_DB_USER`
- `BR_DB_PASSWORD`
- `BR_DB_INICIALIZAR`

Para una base MySQL ya preparada, usar
`BR_DB_INICIALIZAR=false`: los scripts de inicialización
incluyen datos de demostración y no deben repetirse.

## Documentación
- [Arquitectura](docs/ARQUITECTURA.md)
- [Manual de usuario](docs/MANUAL_USUARIO.md)
