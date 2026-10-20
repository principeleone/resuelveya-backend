# Global Architectural Decisions

## 1. Authentication & Security
- Stateless JWT using HMAC-SHA256 (`io.jsonwebtoken.jjwt` 0.12.6).
- Secret key passed via `${JWT_SECRET}` env variable (Base64-encoded, minimum 256 bits).
- Roles: `ADMIN`, `TECNICO`, `CLIENTE`.

## 2. Persistence & Data Modeling
- JPA / Hibernate with MySQL.
- Inheritance strategy: `InheritanceType.JOINED` (`Usuario` base entity, `Cliente` and `Tecnico` joined sub-entities).
- `Usuario` centraliza ubicación (`direccion`, `latitud`, `longitud`) y reputación (`calificacion_promedio`), mientras `Cliente` almacena `puntos_acumulados` y `Tecnico` atributos profesionales.
- Foreign keys and database integrity rules enforced.

## 3. API Guidelines
- Base path: `/api/v1`.
- Clean Code: Early returns, immutability, DTO mappings (MapStruct), global exception handling.
- Language policy: Code strictly in English, documentation and walkthroughs in Spanish.
