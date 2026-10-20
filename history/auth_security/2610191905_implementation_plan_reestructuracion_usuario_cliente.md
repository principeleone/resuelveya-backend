---
module: auth_security
type: implementation_plan
status: draft
related_task: 2610191905
created: 2610191905
---

# Plan de Implementación: Reestructuración de Entidades Usuario y Cliente

Reorganizar los atributos entre `Usuario`, `Cliente` y `Tecnico` para unificar ubicación y reputación a nivel de usuario base, e introducir el atributo `puntos_acumulados` para `Cliente`.

## Cambios Estructurales
1. **Entidad `Usuario`**:
   - Agregar `direccion` (VARCHAR 255), `latitud` (DOUBLE), `longitud` (DOUBLE), `calificacionPromedio` (DOUBLE DEFAULT 0.0).
2. **Entidad `Cliente`**:
   - Retirar `direccion`, `latitud`, `longitud` (heredados de `Usuario`).
   - Agregar `puntosAcumulados` (INT DEFAULT 0).
3. **Entidad `Tecnico`**:
   - Retirar campo propio `calificacionPromedio` (heredado de `Usuario`).
4. **DTOs y Mappers**:
   - `UsuarioResponseDTO`, `UsuarioRequestDTO`, `PerfilResponseDto`, `ClienteResponseDto`, `ClienteRequestDto`, `FirebaseLoginRequestDto`.
5. **Servicios y Repositorios**:
   - Actualizar asignaciones en `PerfilServiceImpl`, `AuthServiceImpl`, `UsuarioServiceImpl`, `ClienteServiceImpl` y recálculos en `ReseniaServiceImpl`.
6. **Base de Datos y Migración**:
   - Actualizar `schema.sql`.
   - Crear script SQL de migración seguro `migration_v2_usuario_cliente.sql`.

## Criterios de Aceptación
- Compilación Maven limpia (`mvn clean compile` o `./mvnw compile`).
- Las columnas en MySQL se migran sin pérdida de datos en despliegue.
- DTOs y controladores mantienen compatibilidad hacia atrás para clientes REST.
