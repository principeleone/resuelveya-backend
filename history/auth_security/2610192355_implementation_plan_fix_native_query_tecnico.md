---
module: auth_security
type: implementation_plan
status: in_progress
related_task: 2610192355
created: 2610192355
---

# Plan de Implementación: Corrección de Consulta Nativa en TecnicoRepository

## Causa Raíz
Durante la migración v2, la columna `calificacion_promedio` fue movida de la tabla `tecnico` a la tabla `usuario` (`ALTER TABLE tecnico DROP COLUMN calificacion_promedio;`). Sin embargo, en `TecnicoRepository.java`, la query nativa:
```sql
INSERT IGNORE INTO tecnico (usuario_id, anios_experiencia, calificacion_promedio) VALUES (:usuarioId, 0, 0.0)
```
continúa intentando insertar en la columna inexistente `calificacion_promedio` de la tabla `tecnico`. Esto dispara un error SQL (`Unknown column 'calificacion_promedio' in 'field list'`) y provoca el HTTP 500 al registrar un técnico. En cambio, para `CLIENTE` se ejecuta `INSERT IGNORE INTO cliente (usuario_id) VALUES (:usuarioId)` que es válida y por eso responde 201 OK.

## Tareas
1. Modificar `TecnicoRepository.java` para remover `calificacion_promedio` de la sentencia SQL `INSERT IGNORE`.
2. Validar compilación con `mvn clean compile`.
3. Crear walkthrough y documentar la solución.
