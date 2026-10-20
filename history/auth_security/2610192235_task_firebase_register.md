---
module: auth_security
type: task
status: done
related_task: 2610192235
created: 2610192235
---

# Tareas: Endpoint Firebase Register

- [x] 1. DTOs y Contratos
  - [x] 1.1 Crear `FirebaseRegisterRequestDto` con validaciones y alias de campos
  - [x] 1.2 Agregar alias `correo` en `UsuarioResponseDTO`
- [x] 2. Lógica de Servicio
  - [x] 2.1 Declarar `registrarConFirebase` en `AuthService`
  - [x] 2.2 Implementar `registrarConFirebase` en `AuthServiceImpl`
- [x] 3. Capa Web
  - [x] 3.1 Agregar `POST /api/v1/auth/firebase-register` en `AuthController`
- [x] 4. Documentación y Memoria
  - [x] 4.1 Actualizar `README.md`
  - [x] 4.2 Actualizar memoria (`auth_security.md`, `_contracts.md`)
  - [x] 4.3 Generar `walkthrough`
