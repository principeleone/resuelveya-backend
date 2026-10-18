---
module: solicitudes
type: task
status: done
related_task: 2610181615
created: 2610181615
---

# Tareas: Funcionalidad de Solicitud y Aceptación de Servicios

- [x] 1. Base de Datos & Entidades
  - [x] 1.1 Crear enum `EstadoSolicitud` (`PENDIENTE`, `ACEPTADA`, `RECHAZADA`, `CANCELADA`, `COMPLETADA`).
  - [x] 1.2 Crear entidad `SolicitudServicio` con relaciones a `Cliente`, `Tecnico`, `Servicio`.
  - [x] 1.3 Actualizar `schema.sql` con la tabla `solicitud_servicio`.
- [x] 2. Acceso a Datos
  - [x] 2.1 Crear interfaz `SolicitudServicioRepository` extendiendo `JpaRepository`.
  - [x] 2.2 Agregar consultas por `cliente.id` y `tecnico.id`, ordenadas por `createdAt DESC`.
- [x] 3. DTOs y Mappers
  - [x] 3.1 Crear `CrearSolicitudRequestDto`, `RechazarSolicitudRequestDto`.
  - [x] 3.2 Crear `SolicitudResponseDto`.
  - [x] 3.3 Crear mapper MapStruct `SolicitudServicioMapper`.
- [x] 4. Lógica de Negocio (Service)
  - [x] 4.1 Definir contrato en `SolicitudServicioService`.
  - [x] 4.2 Implementar validaciones y transiciones de estado en `SolicitudServicioServiceImpl`.
- [x] 5. Controlador REST & Seguridad
  - [x] 5.1 Implementar `SolicitudServicioController` con `@RequestMapping("/api/v1/solicitudes")`.
  - [x] 5.2 Configurar seguridad por roles (`ROLE_CLIENTE`, `ROLE_TECNICO`) en `SecurityConfig`.
- [x] 6. Verificación & Documentación
  - [x] 6.1 Revisar consistencia de tipos y nombres.
  - [x] 6.2 Actualizar `README.md` con los nuevos endpoints para el equipo móvil.
