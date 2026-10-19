package com.resuelveya.resuelve_api.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:}")
    private String firebaseConfigPath;

    @PostConstruct
    public void init() {
        if (!FirebaseApp.getApps().isEmpty()) {
            log.info("FirebaseApp ya se encuentra inicializado.");
            return;
        }

        try {
            InputStream serviceAccount = resolveServiceAccount();
            if (serviceAccount == null) {
                log.warn("Firebase: No se encontró archivo de credenciales. "
                        + "El endpoint /api/v1/auth/firebase-login no estará disponible hasta que se configure.");
                return;
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK inicializado correctamente.");
        } catch (Exception exception) {
            log.error("Error al inicializar Firebase Admin SDK: {}. "
                    + "El endpoint de login con Firebase no funcionará.", exception.getMessage());
        }
    }

    private InputStream resolveServiceAccount() {
        // Prioridad 1: ruta explícita del archivo vía propiedad / variable de entorno
        if (firebaseConfigPath != null && !firebaseConfigPath.isBlank()) {
            try {
                log.info("Firebase: Cargando credenciales desde ruta explícita: {}", firebaseConfigPath);
                return new FileInputStream(firebaseConfigPath);
            } catch (Exception exception) {
                log.warn("Firebase: No se pudo leer el archivo en la ruta configurada: {}", firebaseConfigPath);
            }
        }

        // Prioridad 2: recurso en el classpath
        InputStream classpathStream = getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");
        if (classpathStream != null) {
            log.info("Firebase: Cargando credenciales desde classpath (firebase-service-account.json).");
            return classpathStream;
        }

        return null;
    }
}
