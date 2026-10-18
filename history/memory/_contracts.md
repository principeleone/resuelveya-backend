# Integration Contracts & Public API

## Service Request Lifecycle (`solicitudes`)
- `POST /api/v1/solicitudes` (Role: `CLIENTE`): Create new service request with status `PENDIENTE`.
- `GET /api/v1/solicitudes/cliente`: List requests created by authenticated client.
- `GET /api/v1/solicitudes/tecnico`: List requests addressed to authenticated technician.
- `GET /api/v1/solicitudes/{id}`: Detailed request view (Authorized for either the requesting client, assigned technician, or admin).
- `PATCH /api/v1/solicitudes/{id}/aceptar` (Role: `TECNICO`): Transition status `PENDIENTE` -> `ACEPTADA`.
- `PATCH /api/v1/solicitudes/{id}/rechazar` (Role: `TECNICO`): Transition status `PENDIENTE` -> `RECHAZADA`.
- `PATCH /api/v1/solicitudes/{id}/cancelar` (Role: `CLIENTE`): Transition status `PENDIENTE` -> `CANCELADA`.
- `PATCH /api/v1/solicitudes/{id}/completar` (Role: `TECNICO` or `CLIENTE`): Transition status `ACEPTADA` -> `COMPLETADA`.
