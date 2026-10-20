---
module: solicitudes
code_paths:
  - src/main/java/com/resuelveya/resuelve_api/business/data/entity/SolicitudServicio.java
  - src/main/java/com/resuelveya/resuelve_api/business/data/entity/enums/EstadoSolicitud.java
  - src/main/java/com/resuelveya/resuelve_api/business/data/repository/SolicitudServicioRepository.java
  - src/main/java/com/resuelveya/resuelve_api/business/domain/service/SolicitudServicioService.java
  - src/main/java/com/resuelveya/resuelve_api/business/api/controller/SolicitudServicioController.java
updated: 2610192225
---

# Module: Solicitudes de Servicio

## Business Rules & Invariants
- Un cliente solo puede solicitar un servicio activo perteneciente a un técnico existente o solicitar directamente a un técnico.
- El estado inicial de toda solicitud es `PENDIENTE`.
- Solo el técnico destinatario puede `ACEPTAR` o `RECHAZAR` una solicitud pendiente.
- Solo el técnico asignado puede `INICIAR` una solicitud en estado `ACEPTADA`, transicionándola a `EN_PROCESO`.
- Solo el cliente emisor puede `CANCELAR` una solicitud en estado `PENDIENTE` o `ACEPTADA`.
- Una solicitud en estado `ACEPTADA` o `EN_PROCESO` puede pasar a `COMPLETADA` por cualquiera de las partes.
- No se permiten transiciones desde estados terminales (`RECHAZADA`, `CANCELADA`, `COMPLETADA`).

## Domain Invariants
- `SolicitudServicio` vincula obligatoriamente: `cliente`, `tecnico`, `servicio` (o categoría), `estado`, `fechaSolicitud`, `direccionAtencion`.
