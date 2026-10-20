---
module: solicitudes
type: task
status: done
related_task: 2610192225
created: 2610192225
---

# Tareas: Estado EN_PROCESO y Endpoint Iniciar Solicitud

- [x] 1. Enum y Dominio
  - [x] 1.1 Agregar `EN_PROCESO` a `EstadoSolicitud`
- [x] 2. Capa de Servicio
  - [x] 2.1 Agregar método `iniciarSolicitud` en `SolicitudServicioService`
  - [x] 2.2 Implementar `iniciarSolicitud` en `SolicitudServicioServiceImpl`
  - [x] 2.3 Permitir que `completarSolicitud` admita solicitudes en estado `EN_PROCESO`
- [x] 3. Capa Web y Seguridad
  - [x] 3.1 Agregar `PATCH /api/v1/solicitudes/{id}/iniciar` en `SolicitudServicioController`
  - [x] 3.2 Proteger el endpoint en `SecurityConfig` para rol `TECNICO`
- [x] 4. Verificación y Documentación
  - [x] 4.1 Actualizar `README.md`
  - [x] 4.2 Actualizar memoria (`solicitudes.md`, `_contracts.md`)
  - [x] 4.3 Generar `walkthrough`
