---
module: auth_security
type: implementation_plan
status: done
related_task: 2610192235
created: 2610192235
---

# Plan de Implementación: Endpoint de Registro con Firebase (/api/v1/auth/firebase-register)

## 1. Contexto y Objetivos
Proveer un endpoint específico para el registro de usuarios que han sido autenticados vía Firebase en la aplicación móvil, donde las credenciales son gestionadas por Firebase y la app envía los datos de perfil para crear el registro en la base de datos MySQL de ResuelveYa.

## 2. Requerimientos
- **Ruta:** `POST /api/v1/auth/firebase-register`
- **Permisos:** Público (`permitAll`)
- **Campos Request:**
  - `nombre` (obligatorio, `@NotBlank`)
  - `correo` (obligatorio, `@NotBlank`, `@Email`, con alias `@JsonAlias({"email", "correo"})`)
  - `telefono` (opcional / nullable)
  - `direccion` (opcional)
  - `rol` (obligatorio, `CLIENTE` o `TECNICO`)
  - Soporte opcional para `ciudad`, `latitud`, `longitud`, `fotoUrl`
- **Comportamiento:**
  - Valida que el correo no esté ya registrado en la base de datos (lanza `RecursoDuplicadoException` si existe).
  - Asigna contraseña cifrada aleatoria interna (placeholder UUID), ya que la autenticación es delegada a Firebase.
  - Guarda la entidad correspondiente según el rol (`Cliente` o `Tecnico`) garantizando la integridad de herencia `JOINED`.
  - Retorna `UsuarioResponseDTO` con código de estado HTTP `201 CREATED`.

## 3. Modificaciones a Realizar
1. Crear DTO [FirebaseRegisterRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/dto/FirebaseRegisterRequestDto.java).
2. Añadir método `registrarConFirebase(FirebaseRegisterRequestDto requestDto)` en [AuthService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/AuthService.java).
3. Implementar en [AuthServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/impl/AuthServiceImpl.java).
4. Agregar getter `getCorreo()` en [UsuarioResponseDTO.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/usuario/UsuarioResponseDTO.java) para interoperabilidad transparente.
5. Exponer endpoint en [AuthController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/controller/AuthController.java).
6. Actualizar [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md) y memoria (`auth_security.md`, `_contracts.md`, `_index.md`).
