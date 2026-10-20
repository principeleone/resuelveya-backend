package com.resuelveya.resuelve_api.security.api.dto;

import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirebaseLoginRequestDto(
        @NotBlank(message = "El token de Firebase es obligatorio")
        String firebaseToken,

        Rol rol,

        @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
        String nombre,

        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
        String direccion,

        @Size(max = 100, message = "La ciudad no debe superar los 100 caracteres")
        String ciudad,

        Double latitud,

        Double longitud
) {
}

