---
module: auth_security
type: implementation_plan
status: done
related_task: 2610181632
created: 2610181632
---

# Plan de Implementación: Login con Firebase Auth (Móvil)

## 1. Objetivo y Arquitectura
Implementar el endpoint `POST /api/v1/auth/firebase-login` mediante el patrón **Token Exchange**:
1. La aplicación móvil se autentica con Firebase (Google Sign-In, Email, etc.) y obtiene un `firebaseToken` (ID Token de Firebase).
2. La app móvil envía dicho token al backend en `POST /api/v1/auth/firebase-login`.
3. El backend verifica la autenticidad del token con el SDK oficial de **Firebase Admin**.
4. Si el usuario ya existe en la base de datos MySQL (`usuario`), se cargan sus roles y datos.
5. Si no existe, se registra automáticamente en MySQL vinculando su rol (`CLIENTE` o `TECNICO`) y nombre proporcionado por Firebase.
6. El backend genera el JWT propio de ResuelveYa y devuelve el mismo `LoginResponseDto` para que la app móvil consuma todos los endpoints existentes con `Authorization: Bearer <token>`.

---

## 2. Componentes Técnicos
1. **Dependencia Maven:** `com.google.firebase:firebase-admin:9.3.0` en `pom.xml`.
2. **Configuración Resiliente:** `FirebaseConfig.java` que inicializa `FirebaseApp` leyendo desde:
   - Variable de entorno o propiedad `firebase.config.path`
   - O recurso del classpath `firebase-service-account.json`
   - Manejo graceful: si el archivo aún no existe en desarrollo local, no rompe el inicio de Spring Boot, advirtiendo con un log.
3. **Servicio Verificador:** `FirebaseService.java` para encapsular la validación de tokens de Firebase (`verifyIdToken`).
4. **DTO de Entrada:** `FirebaseLoginRequestDto(String firebaseToken, Rol rol)`.
5. **Ampliación de `AuthService` y `AuthServiceImpl`:** Método `loginConFirebase(FirebaseLoginRequestDto requestDto)`.
6. **Ampliación de `AuthController`:** Endpoint `POST /api/v1/auth/firebase-login`.
7. **Documentación:** Actualizar `README.md` y `application.yaml`.
