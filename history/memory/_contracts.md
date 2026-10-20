# Integration Contracts & Public API

## Service Request Lifecycle (`solicitudes`)
- `POST /api/v1/solicitudes` (Role: `CLIENTE`): Create new service request with status `PENDIENTE`.
- `GET /api/v1/solicitudes/cliente`: List requests created by authenticated client.
- `GET /api/v1/solicitudes/tecnico`: List requests addressed to authenticated technician.
- `GET /api/v1/solicitudes/{id}`: Detailed request view (Authorized for either the requesting client, assigned technician, or admin).
- `PATCH /api/v1/solicitudes/{id}/aceptar` (Role: `TECNICO`): Transition status `PENDIENTE` -> `ACEPTADA`.
- `PATCH /api/v1/solicitudes/{id}/iniciar` (Role: `TECNICO`): Transition status `ACEPTADA` -> `EN_PROCESO`.
- `PATCH /api/v1/solicitudes/{id}/rechazar` (Role: `TECNICO`): Transition status `PENDIENTE` -> `RECHAZADA`.
- `PATCH /api/v1/solicitudes/{id}/cancelar` (Role: `CLIENTE`): Transition status `PENDIENTE` / `ACEPTADA` -> `CANCELADA`.
- `PATCH /api/v1/solicitudes/{id}/completar` (Role: `TECNICO` or `CLIENTE`): Transition status `ACEPTADA` / `EN_PROCESO` -> `COMPLETADA`.

## Authentication & Registration (`auth_security`)
- `POST /api/v1/auth/registro`: Standard registration requiring password. Returns `UsuarioResponseDTO`.
- `POST /api/v1/auth/firebase-register`: Firebase registration without password (placeholder generated). Returns `UsuarioResponseDTO`.
- `POST /api/v1/auth/login`: Standard username/password login. Returns `LoginResponseDto` with JWT.
- `POST /api/v1/auth/firebase-login`: Token exchange with Firebase ID token. Returns `LoginResponseDto` with JWT.
