---
module: auth_security
type: walkthrough
status: done
related_task: 2610182153
created: 2610182155
---

# Walkthrough: Datos de Perfil en Login/Registro de Firebase

Se habilitaron campos adicionales opcionales en el endpoint `POST /api/v1/auth/firebase-login` para que las aplicaciones cliente (Android/iOS) envíen el perfil completo en una única petición al autenticar mediante Firebase.

## Cambios Realizados

1. **[FirebaseLoginRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/dto/FirebaseLoginRequestDto.java)**:
   - Se agregaron los atributos opcionales `nombre`, `telefono`, `direccion` y `ciudad`.

2. **[AuthServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/impl/AuthServiceImpl.java#L152)**:
   - En el registro de nuevo usuario desde Firebase, se persisten `telefono`, `ciudad` y `direccion` (en `Cliente`).
   - Si el usuario ya existía previamente pero tenía campos vacíos, se actualizan automáticamente con los valores provistos.

3. **[README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md#L86)**:
   - Se actualizó el cuerpo de ejemplo con los nuevos campos disponibles.
