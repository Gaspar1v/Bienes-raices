CREATE DATABASE IF NOT EXISTS bienes_raices
    CHARACTER SET utf8mb4 COLLATE utf8mb4_spanish_ci;

CREATE USER IF NOT EXISTS 'br_app'@'localhost' IDENTIFIED BY 'CambiarEstaClave#2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON bienes_raices.* TO 'br_app'@'localhost';
FLUSH PRIVILEGES;
