package com.resuelveya.resuelve_api.business.api.dto.solicitud;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CrearSolicitudRequestDto(
        Long servicioId,

        Long tecnicoId,

        @NotBlank(message = "La descripción del servicio solicitado es obligatoria")
        @Size(max = 1000, message = "La descripción no debe superar los 1000 caracteres")
        String descripcion,

        @NotBlank(message = "La dirección de atención es obligatoria")
        @Size(max = 255, message = "La dirección no debe superar los 255 caracteres")
        String direccionAtencion,

        Double latitud,

        Double longitud,

        LocalDateTime fechaProgramada,

        BigDecimal precioAcordado
) {
}
