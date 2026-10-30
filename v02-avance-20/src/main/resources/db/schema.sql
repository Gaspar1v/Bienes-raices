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
