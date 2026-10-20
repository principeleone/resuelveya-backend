---
module: auth_security
type: walkthrough
status: done
related_task: 2610191905
created: 2610191913
---

# Walkthrough: Reestructuración de Entidades Usuario y Cliente

Se completó la reorganización de los modelos de datos y DTOs para unificar la ubicación y reputación a nivel de `Usuario`, permitiendo que tanto clientes como técnicos posean `direccion`, `latitud`, `longitud` y `calificacion_promedio`. Además, se agregó el campo `puntos_acumulados` exclusivamente para `Cliente`.

## Cambios Realizados

1. **Entidades JPA**:
   - [Usuario.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/Usuario.java): Se agregaron los atributos `direccion`, `latitud`, `longitud` y `calificacionPromedio` (default `0.0`).
   - [Cliente.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/Cliente.java): Se retiraron `direccion`, `latitud`, `longitud` (ahora se heredan de `Usuario`) y se agregó `puntosAcumulados` (default `0`).
   - [Tecnico.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/data/entity/Tecnico.java): Se removió la columna propia `calificacionPromedio` para usar directamente la heredada de `Usuario`.

2. **DTOs Actualizados**:
   - [UsuarioRequestDTO.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/usuario/UsuarioRequestDTO.java): Admite `direccion`, `latitud`, `longitud`.
   - [UsuarioResponseDTO.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/usuario/UsuarioResponseDTO.java): Devuelve `direccion`, `latitud`, `longitud` y `calificacionPromedio`.
   - [PerfilResponseDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/usuario/PerfilResponseDto.java): Muestra ubicación y reputación general, y `puntosAcumulados` para clientes.
   - [ClienteRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/cliente/ClienteRequestDto.java) y [ClienteResponseDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/cliente/ClienteResponseDto.java): Soportan `puntosAcumulados` y `calificacionPromedio`.
   - [FirebaseLoginRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/dto/FirebaseLoginRequestDto.java): Recibe coordenadas `latitud` y `longitud`.

3. **Capa de Negocio y Servicios**:
   - [PerfilServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/PerfilServiceImpl.java): Actualiza y mapea los nuevos campos a nivel de `Usuario` y retorna `puntosAcumulados` para el cliente.
   - [AuthServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/impl/AuthServiceImpl.java): Guarda dirección y coordenadas directamente en `Usuario`, e inicializa los clientes con `puntosAcumulados = 0`.
   - [ClienteServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/ClienteServiceImpl.java) y [UsuarioServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/domain/service/impl/UsuarioServiceImpl.java): Aseguran la inicialización de `puntosAcumulados = 0` y `calificacionPromedio = 0.0`.

4. **Base de Datos y Migración**:
   - [schema.sql](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/resources/schema.sql): Tablas `usuario`, `cliente` y `tecnico` actualizadas.
   - [migration_v2_usuario_cliente.sql](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/resources/migration_v2_usuario_cliente.sql): Script SQL seguro para migrar datos existentes y actualizar esquemas en MySQL (Railway / Local).
