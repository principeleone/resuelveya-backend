---
module: solicitudes
type: walkthrough
status: done
related_task: 2610192225
created: 2610192230
---

# Walkthrough: Incorporación del Estado EN_PROCESO y Endpoint Iniciar Solicitud

Se agregó el estado `EN_PROCESO` al ciclo de vida de las solicitudes de servicio en el backend de ResuelveYa, habilitando el endpoint para que el técnico pase el servicio de `ACEPTADA` a `EN_PROCESO`, y actualizando la documentación en `README.md`.

## 1. Cambios Realizados

1. **Enum de Estados ([EstadoSolicitud.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/enums/EstadoSolicitud.java)):**
   - Se añadió el valor `EN_PROCESO`.
   - Estados vigentes: `PENDIENTE`, `ACEPTADA`, `EN_PROCESO`, `RECHAZADA`, `CANCELADA`, `COMPLETADA`.

2. **Capa de Negocio:**
   - [SolicitudServicioService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/SolicitudServicioService.java): Se declaró el método `iniciarSolicitud(String emailTecnico, Long solicitudId)`.
   - [SolicitudServicioServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/SolicitudServicioServiceImpl.java):
     - Se implementó `iniciarSolicitud`: valida que quien ejecuta sea el técnico asignado y que el estado sea estrictamente `ACEPTADA`, cambiando el estado a `EN_PROCESO`.
     - Se actualizó `completarSolicitud`: ahora admite completar solicitudes que se encuentren en estado `ACEPTADA` o `EN_PROCESO`.

3. **Capa Web y Seguridad:**
   - [SolicitudServicioController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/controller/SolicitudServicioController.java):
     - Se agregó `@PatchMapping("/{id}/iniciar")`.
   - [SecurityConfig.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/config/SecurityConfig.java):
     - Se configuró la ruta `PATCH /api/v1/solicitudes/*/iniciar` restringida a `ROLE_TECNICO`.

4. **Documentación:**
   - [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md):
     - Se agregó la tabla de flujo de estados resultante en BD.
     - Se incorporó la sección `9.6. Iniciar atención / servicio (Técnico / Proveedor)`.
     - Se actualizó la Matriz de Permisos con `/api/v1/solicitudes/{id}/iniciar`.

---

## 2. Guía de Pruebas y Validación

### Flujo Completo de la Solicitud
1. **Crear solicitud:** `POST /api/v1/solicitudes` (Rol: `CLIENTE`) -> Estado: `PENDIENTE`.
2. **Aceptar solicitud:** `PATCH /api/v1/solicitudes/{id}/aceptar` (Rol: `TECNICO`) -> Estado: `ACEPTADA`.
3. **Iniciar servicio:** `PATCH /api/v1/solicitudes/{id}/iniciar` (Rol: `TECNICO`) -> Estado: `EN_PROCESO`.
4. **Completar servicio:** `PATCH /api/v1/solicitudes/{id}/completar` (Rol: `CLIENTE` o `TECNICO`) -> Estado: `COMPLETADA`.
