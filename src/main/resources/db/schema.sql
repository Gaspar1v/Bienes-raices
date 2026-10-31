CREATE TABLE IF NOT EXISTS usuario (
    id_usuario        INT AUTO_INCREMENT PRIMARY KEY,
    username          VARCHAR(50)  NOT NULL UNIQUE,
    password_hash     VARCHAR(200) NOT NULL,
    nombres           VARCHAR(100) NOT NULL,
    rol               VARCHAR(20)  NOT NULL,
    activo            BOOLEAN      NOT NULL DEFAULT TRUE,
    intentos_fallidos INT          NOT NULL DEFAULT 0,
    bloqueado_hasta   TIMESTAMP    NULL,
    CONSTRAINT ck_usuario_rol CHECK (rol IN ('CLIENTE','PROPIETARIO','AGENTE','GERENTE','ASISTENTE','ADMINISTRADOR'))
);

CREATE TABLE IF NOT EXISTS propietario (
    id_propietario INT AUTO_INCREMENT PRIMARY KEY,
    nombres        VARCHAR(100) NOT NULL,
    dni            CHAR(8)      NOT NULL UNIQUE,
    telefono       VARCHAR(9)   NOT NULL,
    email          VARCHAR(120) NULL
);

CREATE TABLE IF NOT EXISTS inmueble (
    id_inmueble        INT AUTO_INCREMENT PRIMARY KEY,
    tipo               VARCHAR(20)   NOT NULL,
    operacion_ofrecida VARCHAR(10)   NOT NULL,
    metraje            DECIMAL(10,2) NOT NULL,
    distrito           VARCHAR(60)   NOT NULL,
    direccion          VARCHAR(150)  NOT NULL,
    precio             DECIMAL(12,2) NOT NULL,
    habitaciones       INT           NULL,
    piso               INT           NULL,
    cochera            BOOLEAN       NOT NULL DEFAULT FALSE,
    acabados           VARCHAR(200)  NULL,
    referencias        VARCHAR(300)  NULL,
    fecha_construccion DATE          NULL,
    id_propietario     INT           NOT NULL,
    estado             VARCHAR(20)   NOT NULL DEFAULT 'NUEVO_INGRESO',
    fecha_registro     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_inmueble_propietario FOREIGN KEY (id_propietario) REFERENCES propietario (id_propietario),
    CONSTRAINT ck_inmueble_tipo CHECK (tipo IN ('CASA','DEPARTAMENTO','LOCAL_COMERCIAL','OFICINA','TERRENO')),
    CONSTRAINT ck_inmueble_operacion CHECK (operacion_ofrecida IN ('VENTA','ALQUILER')),
    CONSTRAINT ck_inmueble_estado CHECK (estado IN ('NUEVO_INGRESO','EN_RESERVA','VENDIDO','ALQUILADO','DADO_DE_BAJA')),
    CONSTRAINT ck_inmueble_valores CHECK (metraje > 0 AND precio > 0)
);

CREATE TABLE IF NOT EXISTS historial_estado (
    id_historial    INT AUTO_INCREMENT PRIMARY KEY,
    id_inmueble     INT         NOT NULL,
    estado_anterior VARCHAR(20) NULL,
    estado_nuevo    VARCHAR(20) NOT NULL,
    fecha           TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario      INT         NOT NULL,
    CONSTRAINT fk_historial_inmueble FOREIGN KEY (id_inmueble) REFERENCES inmueble (id_inmueble),
    CONSTRAINT fk_historial_usuario FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario)
);

CREATE TABLE IF NOT EXISTS pedido (
    id_pedido            INT AUTO_INCREMENT PRIMARY KEY,
    tipo_operacion       VARCHAR(10)   NOT NULL,
    tipo_propiedad       VARCHAR(20)   NULL,
    distrito             VARCHAR(60)   NULL,
    precio_maximo        DECIMAL(12,2) NULL,
    metraje_minimo       DECIMAL(10,2) NULL,
    habitaciones_minimas INT           NULL,
    nombre_contacto      VARCHAR(100)  NOT NULL,
    email                VARCHAR(120)  NOT NULL,
    telefono             VARCHAR(9)    NOT NULL,
    fecha                TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado               VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE'
);

CREATE TABLE IF NOT EXISTS operacion (
    id_operacion   INT AUTO_INCREMENT PRIMARY KEY,
    id_inmueble    INT           NOT NULL,
    tipo_operacion VARCHAR(10)   NOT NULL,
    cliente_nombre VARCHAR(100)  NOT NULL,
    cliente_dni    CHAR(8)       NOT NULL,
    id_agente      INT           NOT NULL,
    fecha_inicio   DATE          NOT NULL,
    fecha_fin      DATE          NULL,
    monto          DECIMAL(12,2) NOT NULL,
    comision       DECIMAL(12,2) NOT NULL,
    fecha_registro TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_operacion_inmueble FOREIGN KEY (id_inmueble) REFERENCES inmueble (id_inmueble),
    CONSTRAINT fk_operacion_agente FOREIGN KEY (id_agente) REFERENCES usuario (id_usuario),
    CONSTRAINT ck_operacion_tipo CHECK (tipo_operacion IN ('VENTA','ALQUILER')),
    CONSTRAINT ck_operacion_monto CHECK (monto > 0 AND comision >= 0)
);

CREATE TABLE IF NOT EXISTS solicitud_captacion (
    id_solicitud         INT AUTO_INCREMENT PRIMARY KEY,
    operacion_ofrecida   VARCHAR(10)   NOT NULL,
    tipo                 VARCHAR(20)   NOT NULL,
    metraje              DECIMAL(10,2) NOT NULL,
    distrito             VARCHAR(60)   NOT NULL,
    direccion            VARCHAR(150)  NOT NULL,
    precio               DECIMAL(12,2) NOT NULL,
    habitaciones         INT           NULL,
    piso                 INT           NULL,
    cochera              BOOLEAN       NOT NULL DEFAULT FALSE,
    acabados             VARCHAR(200)  NULL,
    referencias          VARCHAR(300)  NULL,
    fecha_construccion   DATE          NULL,
    propietario_nombres  VARCHAR(100)  NOT NULL,
    propietario_dni      CHAR(8)       NOT NULL,
    propietario_telefono VARCHAR(9)    NOT NULL,
    propietario_email    VARCHAR(120)  NOT NULL,
    estado               VARCHAR(20)   NOT NULL DEFAULT 'PENDIENTE',
    observaciones        VARCHAR(300)  NULL,
    id_revisor           INT           NULL,
    fecha_registro       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_revision       TIMESTAMP     NULL,
    id_inmueble          INT           NULL,
    CONSTRAINT fk_solicitud_revisor FOREIGN KEY (id_revisor) REFERENCES usuario (id_usuario),
    CONSTRAINT fk_solicitud_inmueble FOREIGN KEY (id_inmueble) REFERENCES inmueble (id_inmueble),
    CONSTRAINT ck_solicitud_estado CHECK (estado IN ('PENDIENTE','OBSERVADA','ACEPTADA')),
    CONSTRAINT ck_solicitud_operacion CHECK (operacion_ofrecida IN ('VENTA','ALQUILER')),
    CONSTRAINT ck_solicitud_valores CHECK (metraje > 0 AND precio > 0)
);

CREATE INDEX idx_inmueble_busqueda ON inmueble (estado, operacion_ofrecida, tipo);
CREATE INDEX idx_operacion_fecha ON operacion (fecha_inicio);
CREATE INDEX idx_solicitud_estado ON solicitud_captacion (estado);
