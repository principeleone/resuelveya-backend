---
module: auth_security
type: implementation_plan
status: done
related_task: 2610182153
created: 2610182153
---

# Plan: Enriquecer Firebase Login con Datos de Perfil (Móvil)

## 1. Problema
Al registrar un usuario mediante Firebase Auth en la app móvil, Firebase solo gestiona email y contraseña.
El endpoint `/api/v1/auth/firebase-login` únicamente aceptaba `firebaseToken` y `rol`. Se requiere permitir que la app móvil envíe en la misma petición datos complementarios (`nombre`, `telefono`, `direccion`, `ciudad`) para persistirlos directamente en MySQL sin necesidad de llamadas HTTP adicionales.

## 2. Cambios Propuestos
1. **`FirebaseLoginRequestDto`**:
   - Añadir campos opcionales: `nombre`, `telefono`, `direccion`, `ciudad`.
2. **`AuthServiceImpl`**:
   - Considerar `requestDto.nombre()` con prioridad sobre el nombre de Firebase.
   - Asignar `telefono`, `ciudad` y `direccion` (para Cliente) al momento de registrar o completar el perfil.
3. **`README.md`**:
   - Actualizar la documentación del endpoint con el nuevo cuerpo de petición enriquecido.
