---
module: auth_security
type: task
status: done
related_task: null
created: 2610191905
---

# Tareas: Reestructuración de Entidades Usuario y Cliente

- [x] 1. Modificar entidades JPA (`Usuario`, `Cliente`, `Tecnico`).
- [x] 2. Actualizar DTOs (`UsuarioRequestDTO`, `UsuarioResponseDTO`, `PerfilResponseDto`, `ClienteRequestDto`, `ClienteResponseDto`, `FirebaseLoginRequestDto`).
- [x] 3. Actualizar servicios y mappers (`UsuarioServiceImpl`, `PerfilServiceImpl`, `AuthServiceImpl`, `ClienteServiceImpl`, `ReseniaServiceImpl`).
- [x] 4. Actualizar `schema.sql` y crear script de migración SQL `migration_v2_usuario_cliente.sql`.
- [x] 5. Validar consistencia y compatibilidad de tipos en código.
- [x] 6. Generar walkthrough y actualizar memoria.
