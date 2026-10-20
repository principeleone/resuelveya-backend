CREATE DATABASE IF NOT EXISTS resuelveya_db;
USE resuelveya_db;

-- 1. Tabla Especialidad / Categoria
CREATE TABLE IF NOT EXISTS especialidad (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255)
);

-- 2. Tabla Padre Usuario
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    ciudad VARCHAR(100),
    codigo_ubigeo VARCHAR(10),
    rol VARCHAR(20) NOT NULL,
    foto_url VARCHAR(255),
    direccion VARCHAR(255),
    latitud DOUBLE,
    longitud DOUBLE,
    calificacion_promedio DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- 3. Tabla Separada Cliente (Herencia JOINED)
CREATE TABLE IF NOT EXISTS cliente (
    usuario_id BIGINT PRIMARY KEY,
    puntos_acumulados INT DEFAULT 0,
    CONSTRAINT fk_cliente_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE
);

-- 4. Tabla Separada Tecnico (Herencia JOINED)
CREATE TABLE IF NOT EXISTS tecnico (
    usuario_id BIGINT PRIMARY KEY,
    especialidad_id BIGINT,
    anios_experiencia INT DEFAULT 0,
    validacion BOOLEAN DEFAULT FALSE,
    yape_numero VARCHAR(20),
    plin_numero VARCHAR(20),
    presentacion VARCHAR(1000),
    titular_pago VARCHAR(120),
    CONSTRAINT fk_tecnico_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id) ON DELETE CASCADE,
    CONSTRAINT fk_tecnico_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidad(id) ON DELETE SET NULL
);

-- 5. Tabla Servicio
CREATE TABLE IF NOT EXISTS servicio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(120) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    precio_estimado DECIMAL(10,2) NOT NULL,
    tiempo_estimado VARCHAR(50),
    activo BOOLEAN DEFAULT TRUE NOT NULL,
    especialidad_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_servicio_especialidad FOREIGN KEY (especialidad_id) REFERENCES especialidad(id),
    CONSTRAINT fk_servicio_tecnico FOREIGN KEY (tecnico_id) REFERENCES tecnico(usuario_id) ON DELETE CASCADE
);

-- 6. Tabla Reseña
CREATE TABLE IF NOT EXISTS resenia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    calificacion INT NOT NULL,
    comentario VARCHAR(1000),
    cliente_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    servicio_id BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_resenia_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_resenia_tecnico FOREIGN KEY (tecnico_id) REFERENCES tecnico(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_resenia_servicio FOREIGN KEY (servicio_id) REFERENCES servicio(id) ON DELETE CASCADE,
    CONSTRAINT uk_resenia_cliente_servicio UNIQUE (cliente_id, servicio_id)
);

-- 7. Tabla Solicitud de Servicio
CREATE TABLE IF NOT EXISTS solicitud_servicio (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    tecnico_id BIGINT NOT NULL,
    servicio_id BIGINT,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    descripcion VARCHAR(1000) NOT NULL,
    direccion_atencion VARCHAR(255) NOT NULL,
    latitud DOUBLE,
    longitud DOUBLE,
    fecha_programada DATETIME,
    precio_acordado DECIMAL(10,2),
    motivo_cancelacion VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_tecnico FOREIGN KEY (tecnico_id) REFERENCES tecnico(usuario_id) ON DELETE CASCADE,
    CONSTRAINT fk_solicitud_servicio FOREIGN KEY (servicio_id) REFERENCES servicio(id) ON DELETE SET NULL
);

-- Sincronización retroactiva: asegurar filas hijas para usuarios existentes
INSERT IGNORE INTO cliente (usuario_id, puntos_acumulados)
SELECT id, 0 FROM usuario WHERE rol = 'CLIENTE';

INSERT IGNORE INTO tecnico (usuario_id, anios_experiencia, validacion)
SELECT id, 0, FALSE FROM usuario WHERE rol = 'TECNICO';