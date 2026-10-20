-- =============================================================================
-- MIGRATION V2: Reestructuración de Usuario, Cliente y Técnico
-- Fecha: 2026-10-19
-- Compatible con: MySQL 5.7+, MySQL 8.0+, MariaDB, Railway MySQL
-- =============================================================================

USE resuelveya_db;

-- 1. Agregar nuevas columnas a 'usuario'
ALTER TABLE usuario
    ADD COLUMN direccion VARCHAR(255) NULL AFTER foto_url,
    ADD COLUMN latitud DOUBLE NULL AFTER direccion,
    ADD COLUMN longitud DOUBLE NULL AFTER latitud,
    ADD COLUMN calificacion_promedio DOUBLE DEFAULT 0.0 AFTER longitud;

-- 2. Migrar direccion, latitud y longitud existentes desde 'cliente' hacia 'usuario'
UPDATE usuario u
INNER JOIN cliente c ON u.id = c.usuario_id
SET 
    u.direccion = c.direccion,
    u.latitud = c.latitud,
    u.longitud = c.longitud;

-- 3. Migrar calificacion_promedio existente desde 'tecnico' hacia 'usuario'
UPDATE usuario u
INNER JOIN tecnico t ON u.id = t.usuario_id
SET 
    u.calificacion_promedio = COALESCE(t.calificacion_promedio, 0.0);

-- 4. Agregar puntos_acumulados a 'cliente'
ALTER TABLE cliente
    ADD COLUMN puntos_acumulados INT DEFAULT 0;

-- 5. Inicializar puntos_acumulados en 0 para clientes existentes
UPDATE cliente
SET puntos_acumulados = 0
WHERE puntos_acumulados IS NULL;

-- 6. Eliminar columnas migradas de 'cliente'
ALTER TABLE cliente
    DROP COLUMN direccion,
    DROP COLUMN latitud,
    DROP COLUMN longitud;

-- 7. Eliminar columna migrada de 'tecnico'
ALTER TABLE tecnico
    DROP COLUMN calificacion_promedio;
