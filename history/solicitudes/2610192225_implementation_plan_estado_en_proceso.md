---
module: solicitudes
type: implementation_plan
status: done
related_task: 2610192225
created: 2610192225
---

# Plan de Implementación: Estado EN_PROCESO y Endpoint Iniciar Solicitud

## 1. Contexto y Objetivos
Incorporar el estado `EN_PROCESO` al ciclo de vida de `SolicitudServicio` en el backend, habilitando el endpoint `PATCH /api/v1/solicitudes/{id}/iniciar` para que el técnico asignado pueda cambiar el estado de `ACEPTADA` a `EN_PROCESO`. Además, permitir que una solicitud `EN_PROCESO` pueda ser completada mediante `PATCH /api/v1/solicitudes/{id}/completar`, y documentar las rutas y permisos en `README.md`.

## 2. Cambios a Realizar

### 2.1. Entidades y Enums
- Modificar [EstadoSolicitud.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/enums/EstadoSolicitud.java):
  - Agregar `EN_PROCESO`.

### 2.2. Capa de Servicio
- Modificar [SolicitudServicioService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/SolicitudServicioService.java):
  - Agregar declaración `SolicitudResponseDto iniciarSolicitud(String emailTecnico, Long solicitudId)`.
- Modificar [SolicitudServicioServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/SolicitudServicioServiceImpl.java):
  - Implementar `iniciarSolicitud`: validar técnico asignado, validar que el estado sea `ACEPTADA`, transicionar a `EN_PROCESO`.
  - Ajustar `completarSolicitud`: permitir completar tanto en estado `ACEPTADA` como en `EN_PROCESO`.

### 2.3. Capa de Controlador y Seguridad
- Modificar [SolicitudServicioController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/controller/SolicitudServicioController.java):
  - Agregar `@PatchMapping("/{id}/iniciar")`.
- Modificar [SecurityConfig.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/config/SecurityConfig.java):
  - Configurar `.requestMatchers(HttpMethod.PATCH, "/api/v1/solicitudes/*/iniciar").hasRole("TECNICO")`.

### 2.4. Documentación y Memoria
- Actualizar [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md) con el nuevo endpoint y en la matriz de permisos.
- Actualizar [history/memory/solicitudes.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/history/memory/solicitudes.md) y [history/memory/_contracts.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/history/memory/_contracts.md).
