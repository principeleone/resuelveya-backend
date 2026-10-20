package com.resuelveya.resuelve_api.business.api.dto.usuario;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequestDto(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no debe superar los 80 caracteres")
        String nombre,

        @Size(max = 20, message = "El teléfono no debe superar los 20 caracteres")
        String telefono,

        String fotoUrl,

        @Size(max = 100, message = "La ciudad no debe superar los 100 caracteres")
        String ciudad,

        @Size(max = 10, message = "El código ubigeo no debe superar los 10 caracteres")
        String codigoUbigeo,

        // Ubicación (común para Cliente y Técnico)
        @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
        String direccion,

        Double latitud,

        Double longitud,

        // Campos específicos de Técnico
        @Size(max = 1000, message = "La presentación no debe superar los 1000 caracteres")
        String presentacion,

        @Min(value = 0, message = "Los años de experiencia no pueden ser negativos")
        Integer aniosExperiencia,

        Long especialidadId,

        @Size(max = 20, message = "El número de Yape no debe superar los 20 caracteres")
        String yapeNumero,

        @Size(max = 20, message = "El número de Plin no debe superar los 20 caracteres")
        String plinNumero,

        @Size(max = 120, message = "El titular de pago no debe superar los 120 caracteres")
        String titularPago
) {
}
