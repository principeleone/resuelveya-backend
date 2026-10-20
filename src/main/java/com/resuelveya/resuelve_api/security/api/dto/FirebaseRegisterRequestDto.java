package com.resuelveya.resuelve_api.security.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FirebaseRegisterRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no debe superar los 100 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "Ingresa un correo válido")
        @Size(max = 100, message = "El correo no debe superar los 100 caracteres")
        @JsonAlias({"email", "correo"})
        String correo,

        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
        String direccion,

        @NotNull(message = "El rol es obligatorio")
        Rol rol,

        @Size(max = 100, message = "La ciudad no debe superar los 100 caracteres")
        String ciudad,

        Double latitud,

        Double longitud,

        String fotoUrl
) {
}
