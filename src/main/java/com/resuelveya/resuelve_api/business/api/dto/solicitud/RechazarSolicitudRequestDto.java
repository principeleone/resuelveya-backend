package com.resuelveya.resuelve_api.business.api.dto.solicitud;

import jakarta.validation.constraints.Size;

public record RechazarSolicitudRequestDto(
        @Size(max = 500, message = "El motivo no debe superar los 500 caracteres")
        String motivo
) {
}
