package com.resuelveya.resuelve_api.security.domain.service;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.resuelveya.resuelve_api.business.api.exception.OperacionNoPermitidaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FirebaseService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseService.class);

    /**
     * Verifica un ID Token de Firebase y devuelve el token decodificado
     * con el email, nombre y uid del usuario autenticado en Firebase.
     */
    public FirebaseToken verifyIdToken(String firebaseIdToken) {
        if (FirebaseApp.getApps().isEmpty()) {
            throw new OperacionNoPermitidaException(
                    "Firebase Auth no está configurado en el servidor. "
                            + "Contacte al administrador para habilitar el inicio de sesión con proveedores externos.");
        }

        try {
            return FirebaseAuth.getInstance().verifyIdToken(firebaseIdToken);
        } catch (FirebaseAuthException exception) {
            log.warn("Firebase: Token inválido o expirado - {}", exception.getMessage());
            throw new OperacionNoPermitidaException(
                    "El token de Firebase es inválido o ha expirado. Inicie sesión nuevamente en la aplicación.");
        }
    }
}
