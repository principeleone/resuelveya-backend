---
module: solicitudes
type: walkthrough
status: done
related_task: 2610181615
created: 2610181625
---

# Walkthrough: Funcionalidad de Solicitud y Aceptación de Servicios

Se implementó el ciclo de vida completo de solicitudes de servicio para la aplicación móvil y web de **ResuelveYa**.

## 1. Resumen de Cambios Realizados

1. **Entidades & Enums:**
   - [EstadoSolicitud.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/enums/EstadoSolicitud.java): Estados `PENDIENTE`, `ACEPTADA`, `RECHAZADA`, `CANCELADA`, `COMPLETADA`.
   - [SolicitudServicio.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/SolicitudServicio.java): Entidad JPA con relaciones `Cliente`, `Tecnico`, `Servicio`, coordenadas GPS, fecha tentativa, precio y motivo de cancelación.
   - [schema.sql](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/resources/schema.sql): Agregada la tabla `solicitud_servicio` con llaves foráneas y borrado en cascada consistente.

2. **Capa de Persistencia:**
   - [SolicitudServicioRepository.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/repository/SolicitudServicioRepository.java): Métodos de consulta por cliente, técnico y estado ordenados cronológicamente descendente.

3. **DTOs y MapStruct:**
   - [CrearSolicitudRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/solicitud/CrearSolicitudRequestDto.java): DTO de creación con validaciones de campos obligatorios.
   - [RechazarSolicitudRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/solicitud/RechazarSolicitudRequestDto.java): DTO para envío de motivos al rechazar o cancelar.
   - [SolicitudResponseDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/solicitud/SolicitudResponseDto.java): Respuesta enriquecida con información de contacto del cliente y del técnico para la interfaz móvil.
   - [SolicitudServicioMapper.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/mapper/SolicitudServicioMapper.java): Mapper MapStruct.

4. **Lógica de Negocio y Control de Seguridad:**
   - [SolicitudServicioService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/SolicitudServicioService.java) y [SolicitudServicioServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/SolicitudServicioServiceImpl.java):
     - Creación de solicitud vinculando automáticamente al cliente autenticado.
     - Auto-asignación de precio estimado si proviene de un servicio de catálogo.
     - Bloqueo de auto-solicitud (un técnico no puede solicitarse servicios a sí mismo).
     - Validación de que solo el técnico destinatario puede aceptar o rechazar solicitudes pendientes.
     - Validación de que solo el cliente solicitante puede cancelar una solicitud.
     - Marcado de servicio completado una vez aceptado.
   - [SolicitudServicioController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/controller/SolicitudServicioController.java): Endpoints REST `/api/v1/solicitudes/**`.
   - [SecurityConfig.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/config/SecurityConfig.java): Permisos por rol (`ROLE_CLIENTE`, `ROLE_TECNICO`, y endpoints compartidos para participantes).
   - [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md): Documentación completa para el equipo de desarrollo móvil.

---

## 2. Guía de Pruebas (Instrucciones para Postman o App Móvil)

### Paso 1: Autenticación
1. Iniciar sesión como cliente:
   - `POST /api/v1/auth/login` con credenciales de cliente -> Copiar token `CLIENTE_TOKEN`.
2. Iniciar sesión como técnico:
   - `POST /api/v1/auth/login` con credenciales de técnico -> Copiar token `TECNICO_TOKEN`.

### Paso 2: Crear Solicitud (Cliente)
- **Ruta:** `POST /api/v1/solicitudes`
- **Header:** `Authorization: Bearer <CLIENTE_TOKEN>`
- **Body:**
```json
{
  "servicioId": 1,
  "descripcion": "Problemas de fuga en tubería de gas",
  "direccionAtencion": "Av. Los Sauces 450, Surco",
  "latitud": -12.1298,
  "longitud": -77.0282,
  "fechaProgramada": "2026-10-25T14:00:00"
}
```
- **Verificación:** Respuesta `201 Created` con `estado: "PENDIENTE"` y el ID de la solicitud (ej. `id: 1`).

### Paso 3: Proveedor/Técnico Consulta Solicitudes Recibidas
- **Ruta:** `GET /api/v1/solicitudes/tecnico?estado=PENDIENTE`
- **Header:** `Authorization: Bearer <TECNICO_TOKEN>`
- **Verificación:** Aparece la solicitud creada en la lista con los datos del cliente y del servicio.

### Paso 4: Técnico Acepta el Servicio
- **Ruta:** `PATCH /api/v1/solicitudes/1/aceptar`
- **Header:** `Authorization: Bearer <TECNICO_TOKEN>`
- **Verificación:** Respuesta `200 OK` con `estado: "ACEPTADA"`.

*(Alternativa)* Si el técnico no puede atender:
- **Ruta:** `PATCH /api/v1/solicitudes/1/rechazar` con body `{"motivo": "No disponible en ese horario"}` -> `estado: "RECHAZADA"`.

### Paso 5: Finalizar el Servicio
- **Ruta:** `PATCH /api/v1/solicitudes/1/completar`
- **Header:** `Authorization: Bearer <TECNICO_TOKEN>` o `<CLIENTE_TOKEN>`
- **Verificación:** `estado: "COMPLETADA"`. Tras completarse, el cliente puede proceder a calificar con `POST /api/v1/resenias`.
