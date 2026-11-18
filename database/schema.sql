CREATE DATABASE IF NOT EXISTS login CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE login;

CREATE TABLE IF NOT EXISTS roles (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(50)  NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS users (
    id       BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50)  NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email    VARCHAR(120) NOT NULL UNIQUE,
    rol_id   BIGINT       NOT NULL,
    CONSTRAINT fk_users_roles FOREIGN KEY (rol_id) REFERENCES roles (id)
);

INSERT IGNORE INTO roles (name, description) VALUES
    ('USER',  'Usuario estándar'),
    ('ADMIN', 'Administrador');
