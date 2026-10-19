---
module: auth_security
type: task
status: done
related_task: 2610181632
created: 2610181632
---

# Tareas: Integración de Firebase Auth para App Móvil

- [x] 1. Dependencias y Configuración
  - [x] 1.1 Agregar `firebase-admin` a `pom.xml`.
  - [x] 1.2 Configurar propiedad `firebase.config.path` en `application.yaml`.
  - [x] 1.3 Crear `FirebaseConfig.java` con inicialización tolerante a fallos.
- [x] 2. DTOs y Servicios
  - [x] 2.1 Crear `FirebaseLoginRequestDto.java`.
  - [x] 2.2 Crear `FirebaseService.java` para verificar ID tokens de Firebase.
  - [x] 2.3 Ampliar `AuthService` e implementar `loginConFirebase` en `AuthServiceImpl`.
- [x] 3. Controlador REST y Seguridad
  - [x] 3.1 Agregar endpoint `POST /api/v1/auth/firebase-login` en `AuthController`.
  - [x] 3.2 Verificar reglas públicas en `SecurityConfig.java` (ya cubiertas por `/api/v1/auth/**`).
- [x] 4. Verificación & Documentación
  - [x] 4.1 Actualizar `README.md` con la especificación del nuevo endpoint para la app móvil.
