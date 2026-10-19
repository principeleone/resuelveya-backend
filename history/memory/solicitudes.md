---
module: solicitudes
code_paths:
  - src/main/java/com/resuelveya/resuelve_api/business/data/entity/SolicitudServicio.java
  - src/main/java/com/resuelveya/resuelve_api/business/data/entity/enums/EstadoSolicitud.java
  - src/main/java/com/resuelveya/resuelve_api/business/data/repository/SolicitudServicioRepository.java
  - src/main/java/com/resuelveya/resuelve_api/business/domain/service/SolicitudServicioService.java
  - src/main/java/com/resuelveya/resuelve_api/business/api/controller/SolicitudServicioController.java
updated: 2610181625
---

# Module: Solicitudes de Servicio

## Business Rules & Invariants
- Un cliente solo puede solicitar un servicio activo perteneciente a un técnico existente o solicitar directamente a un técnico.
- El estado inicial de toda solicitud es `PENDIENTE`.
- Solo el técnico destinatario puede `ACEPTAR` o `RECHAZAR` una solicitud pendiente.
- Solo el cliente emisor puede `CANCELAR` una solicitud pendiente.
- Una solicitud en estado `ACEPTADA` solo puede pasar a `COMPLETADA` o `CANCELADA`.
- No se permiten transiciones desde estados terminales (`RECHAZADA`, `CANCELADA`, `COMPLETADA`).

## Domain Invariants
- `SolicitudServicio` vincula obligatoriamente: `cliente`, `tecnico`, `servicio` (o categoría), `estado`, `fechaSolicitud`, `direccionAtencion`.
