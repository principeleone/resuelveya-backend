---
module: auth_security
type: walkthrough
status: done
related_task: 2610181632
created: 2610181638
---

# Walkthrough: Login con Firebase Auth para App Móvil

Se implementó el patrón **Token Exchange** que permite a la aplicación móvil autenticarse con proveedores de Firebase (Google, Email/Password, Apple, etc.) y obtener un JWT de ResuelveYa válido para consumir todos los endpoints existentes del backend.

## 1. Resumen de Cambios

### Archivos Nuevos
| Archivo | Propósito |
|---|---|
| [FirebaseConfig.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/config/FirebaseConfig.java) | Inicializa el SDK de Firebase Admin de forma resiliente: intenta ruta explícita (`FIREBASE_CONFIG_PATH`), luego classpath (`firebase-service-account.json`). Si no encuentra el archivo, advierte con log en vez de romper el startup. |
| [FirebaseService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/FirebaseService.java) | Encapsula `FirebaseAuth.getInstance().verifyIdToken()` con manejo de errores descriptivos. |
| [FirebaseLoginRequestDto.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/dto/FirebaseLoginRequestDto.java) | DTO de entrada: `firebaseToken` (obligatorio) y `rol` (opcional, para auto-registro). |

### Archivos Modificados
| Archivo | Cambio |
|---|---|
| [pom.xml](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/pom.xml) | Agregada dependencia `com.google.firebase:firebase-admin:9.3.0`. |
| [application.yaml](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/resources/application.yaml) | Agregada propiedad `firebase.config.path`. |
| [AuthService.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/AuthService.java) | Agregado método `loginConFirebase()` al contrato. |
| [AuthServiceImpl.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/domain/service/impl/AuthServiceImpl.java) | Implementado `loginConFirebase()` con auto-registro desde Firebase y método privado `registrarUsuarioDesdeFirebase()`. |
| [AuthController.java](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/src/main/java/com/resuelveya/resuelve_api/security/api/controller/AuthController.java) | Agregado endpoint `POST /api/v1/auth/firebase-login`. |
| [README.md](file:///c:/Users/eduar/Desktop/CIBERTEC/V%20CICLO/PROG04694%20DESARROLLO%20DE%20APLICACIONES%20WEB%20I/PROYECTO/resuelveya-api/README.md) | Documentación del nuevo endpoint (sección 1.3). |

## 2. Flujo de Autenticación Firebase → Backend

1. La app móvil llama a `firebase.auth().signInWithCredential(googleCredential)` (o email/pass, Apple, etc.).
2. Firebase devuelve un **ID Token** al dispositivo.
3. La app envía ese token al backend: `POST /api/v1/auth/firebase-login { firebaseToken, rol }`.
4. El backend verifica el token con `FirebaseAuth.getInstance().verifyIdToken()`.
5. Si el email del token de Firebase ya existe en MySQL → carga el usuario existente.
6. Si no existe → registra automáticamente al usuario con el rol indicado (CLIENTE por defecto) y la foto de perfil de Google si está disponible.
7. Genera un JWT propio de ResuelveYa con `JwtService.generarToken()`.
8. Devuelve `LoginResponseDto` idéntico al login tradicional.

## 3. Guía de Pruebas

### Pre-requisito: Configurar Firebase
1. Ir a [Firebase Console](https://console.firebase.google.com/) → tu proyecto.
2. Configuración del proyecto → Cuentas de servicio → Generar nueva clave privada.
3. Guardar el archivo JSON como `firebase-service-account.json` y:
   - Colocarlo en `src/main/resources/` (desarrollo), **O**
   - Definir la variable de entorno `FIREBASE_CONFIG_PATH=/ruta/al/archivo.json` (producción).

### Probar desde la app móvil (Flutter/React Native/Android)
```dart
// Ejemplo Flutter con firebase_auth
final userCredential = await FirebaseAuth.instance.signInWithCredential(googleCredential);
final firebaseToken = await userCredential.user!.getIdToken();

// Enviar al backend
final response = await http.post(
  Uri.parse('http://tu-servidor:8080/api/v1/auth/firebase-login'),
  headers: {'Content-Type': 'application/json'},
  body: jsonEncode({'firebaseToken': firebaseToken, 'rol': 'CLIENTE'}),
);
// response.body contiene el JWT de ResuelveYa → guardarlo para llamadas futuras
```

### Probar sin app móvil (desarrollo con Postman)
1. Obtener un ID Token de Firebase manualmente:
   - Usar la API REST de Firebase Auth: `POST https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=<API_KEY>` con email y contraseña.
   - Copiar el campo `idToken` de la respuesta.
2. Enviar al backend: `POST /api/v1/auth/firebase-login` con body `{ "firebaseToken": "<idToken>" }`.
3. Verificar que la respuesta contenga el JWT de ResuelveYa, email, rol e id del usuario.
