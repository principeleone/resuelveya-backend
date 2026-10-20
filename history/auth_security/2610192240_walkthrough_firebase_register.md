---
module: auth_security
type: walkthrough
status: done
related_task: 2610192235
created: 2610192240
---

# Walkthrough: Endpoint de Registro con Firebase (/api/v1/auth/firebase-register)

Se implementó el endpoint `POST /api/v1/auth/firebase-register` para registrar usuarios en la base de datos de ResuelveYa que ya han sido creados o autenticados previamente en Firebase Auth desde la aplicación móvil.

## 1. Resumen de Cambios

1. **DTO de Entrada ([FirebaseRegisterRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/dto/FirebaseRegisterRequestDto.java)):**
   - `nombre`: Obligatorio (`@NotBlank`, máx. 100).
   - `correo`: Obligatorio (`@NotBlank`, `@Email`, máx. 100), con `@JsonAlias({"email", "correo"})` para compatibilidad tanto si el cliente envía `"correo"` o `"email"`.
   - `telefono`: Nullable / opcional (máx. 20).
   - `direccion`: Opcional (máx. 255).
   - `rol`: Obligatorio (`@NotNull`, `CLIENTE` o `TECNICO`).
   - Campos adicionales opcionales: `ciudad`, `latitud`, `longitud`, `fotoUrl`.

2. **DTO de Salida ([UsuarioResponseDTO.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/business/api/dto/usuario/UsuarioResponseDTO.java)):**
   - Se añadió el método `getCorreo()` para que la respuesta JSON incluya tanto `"email"` como `"correo"`, garantizando interoperabilidad inmediata con los modelos del cliente móvil.

3. **Capa de Negocio:**
   - [AuthService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/AuthService.java): Se declaró `registrarConFirebase(FirebaseRegisterRequestDto requestDto)`.
   - [AuthServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/impl/AuthServiceImpl.java):
     - Normaliza y valida que el correo no esté previamente registrado (`RecursoDuplicadoException`).
     - Asigna contraseña cifrada interna segura aleatoria (placeholder UUID).
     - Persiste la entidad correspondiente (`Cliente` o `Tecnico`) garantizando la relación `JOINED` con `Usuario`.

4. **Capa Web:**
   - [AuthController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/controller/AuthController.java):
     - Se expuso `POST /api/v1/auth/firebase-register`, retornando `201 Created` con `UsuarioResponseDTO`.

5. **Documentación:**
   - [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md): Documentado en la sección `1.2. Registro con Firebase (App Móvil)` con ejemplo de request y response.

---

## 2. Guía de Pruebas

### Registro con Firebase desde Postman o App Móvil
- **Método:** `POST`
- **Ruta:** `/api/v1/auth/firebase-register`
- **Header:** `Content-Type: application/json`
- **Body:**
```json
{
  "nombre": "Mariana López",
  "correo": "mariana.lopez@ejemplo.com",
  "telefono": "912345678",
  "direccion": "Av. Primavera 650",
  "rol": "CLIENTE"
}
```
- **Respuesta esperada (201 Created):**
```json
{
  "id": 1,
  "nombre": "Mariana López",
  "email": "mariana.lopez@ejemplo.com",
  "correo": "mariana.lopez@ejemplo.com",
  "telefono": "912345678",
  "rol": "CLIENTE",
  "direccion": "Av. Primavera 650",
  "calificacionPromedio": 0.0
}
```
