-- =============================================================================
-- MIGRATION V2: Reestructuración de Usuario, Cliente y Técnico
-- Fecha: 2026-10-19
-- Descripción:
-- 1. Agrega direccion, latitud, longitud y calificacion_promedio a la tabla usuario.
-- 2. Migra los datos existentes de direccion, latitud y longitud de cliente a usuario.
-- 3. Migra calificacion_promedio existente de tecnico a usuario.
-- 4. Agrega puntos_acumulados a la tabla cliente.
-- 5. Elimina las columnas obsoletas de cliente (direccion, latitud, longitud).
-- 6. Elimina la columna obsoleta de tecnico (calificacion_promedio).
-- =============================================================================

USE resuelveya_db;

-- 1. Agregar nuevas columnas a 'usuario' si no existen
ALTER TABLE usuario
    ADD COLUMN IF NOT EXISTS direccion VARCHAR(255) NULL AFTER foto_url,
    ADD COLUMN IF NOT EXISTS latitud DOUBLE NULL AFTER direccion,
    ADD COLUMN IF NOT EXISTS longitud DOUBLE NULL AFTER latitud,
    ADD COLUMN IF NOT EXISTS calificacion_promedio DOUBLE DEFAULT 0.0 AFTER longitud;

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
    ADD COLUMN IF NOT EXISTS puntos_acumulados INT DEFAULT 0;

-- 5. Inicializar puntos_acumulados en 0 para clientes existentes
UPDATE cliente
SET puntos_acumulados = 0
WHERE puntos_acumulados IS NULL;

-- 6. Eliminar columnas migradas de 'cliente'
ALTER TABLE cliente
    DROP COLUMN IF EXISTS direccion,
    DROP COLUMN IF EXISTS latitud,
    DROP COLUMN IF EXISTS longitud;

-- 7. Eliminar columna migrada de 'tecnico'
ALTER TABLE tecnico
    DROP COLUMN IF EXISTS calificacion_promedio;
