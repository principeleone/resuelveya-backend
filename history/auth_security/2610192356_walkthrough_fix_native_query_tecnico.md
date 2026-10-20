---
module: auth_security
type: walkthrough
status: done
related_task: 2610192355
created: 2610192356
---

# Walkthrough: Corrección de Error 500 al Registrar Técnicos

## Causa del Error
En la migración v2 de la base de datos:
- La columna `calificacion_promedio` fue trasladada de la tabla `tecnico` a la tabla `usuario`.
- La columna fue eliminada de `tecnico` (`DROP COLUMN calificacion_promedio`).
- Sin embargo, en `TecnicoRepository.java`, el método nativo `registrarFilaTecnicoSiNoExiste` contenía:
  ```sql
  INSERT IGNORE INTO tecnico (usuario_id, anios_experiencia, calificacion_promedio) VALUES (:usuarioId, 0, 0.0)
  ```
- Al registrar un `CLIENTE`, se ejecutaba `INSERT IGNORE INTO cliente (usuario_id) VALUES (:usuarioId)` (la cual era válida y devolvía HTTP 201).
- Al registrar un `TECNICO`, la consulta nativa fallaba con `SQLSyntaxErrorException: Unknown column 'calificacion_promedio' in 'field list'`, causando que el backend retorne `HTTP 500 Internal Server Error`.

## Cambios Realizados
- En [TecnicoRepository.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/repository/TecnicoRepository.java#L25):
  Se actualizó la consulta nativa a:
  ```java
  @Modifying
  @Query(value = "INSERT IGNORE INTO tecnico (usuario_id, anios_experiencia) VALUES (:usuarioId, 0)", nativeQuery = true)
  void registrarFilaTecnicoSiNoExiste(@Param("usuarioId") Long usuarioId);
  ```

## Pruebas
1. Desplegar o reiniciar el backend (por ejemplo en Railway o localmente).
2. Probar el registro desde la app móvil seleccionando el rol **PROVEEDOR** (`TECNICO`).
3. El backend responderá `HTTP 201 Created` con el JSON del nuevo técnico registrado exitosamente.
