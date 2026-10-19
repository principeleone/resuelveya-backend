package com.resuelveya.resuelve_api.business.api.dto.solicitud;

import com.resuelveya.resuelve_api.business.data.entity.enums.EstadoSolicitud;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SolicitudResponseDto(
        Long id,
        EstadoSolicitud estado,
        Long clienteId,
        String clienteNombre,
        String clienteTelefono,
        String clienteEmail,
        String clienteFotoUrl,
        Long tecnicoId,
        String tecnicoNombre,
        String tecnicoTelefono,
        String tecnicoEmail,
        String tecnicoFotoUrl,
        Long servicioId,
        String servicioTitulo,
        String descripcion,
        String direccionAtencion,
        Double latitud,
        Double longitud,
        LocalDateTime fechaProgramada,
        BigDecimal precioAcordado,
        String motivoCancelacion,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
