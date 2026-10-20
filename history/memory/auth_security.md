---
module: auth_security
code_paths:
  - src/main/java/com/resuelveya/resuelve_api/security/...
updated: 2610192235
---

# Module: Auth & Security

## Business Rules & Invariants
- El sistema utiliza autenticación stateless basada en JWT (HMAC-SHA256).
- Roles: `CLIENTE`, `TECNICO`, `ADMIN`.
- Registro delegado vía Firebase Auth:
  - `POST /api/v1/auth/firebase-register` registra usuarios creados en Firebase sin requerir contraseña (genera placeholder hash).
  - Recibe `nombre` y `correo` obligatorios, `rol` obligatorio, `telefono` nullable, y `direccion` opcional.
- Soporte para autenticación móvil vía Firebase Auth mediante el patrón Token Exchange:
  - La app móvil se autentica en Firebase y envía el `firebaseToken` al endpoint `/api/v1/auth/firebase-login`.
  - El backend verifica el token con `FirebaseAuth`, busca el usuario en MySQL por email; si no existe, lo registra automáticamente con el rol solicitado (`CLIENTE` por defecto).
  - El backend emite su propio token JWT con las credenciales y roles del sistema.

## Technical Constraints
- Si el archivo de credenciales de Firebase no está configurado, la aplicación no debe caer en startup; debe advertir de forma controlada y fallar solo al invocar el endpoint de Firebase con mensaje claro.
