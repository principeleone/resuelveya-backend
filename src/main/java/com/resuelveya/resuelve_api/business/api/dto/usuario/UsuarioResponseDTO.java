package com.resuelveya.resuelve_api.business.api.dto.usuario;

import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;

public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String email,
        String telefono,
        String fotoUrl,
        String ciudad,
        String codigoUbigeo,
        Rol rol,
        String direccion,
        Double latitud,
        Double longitud,
        Double calificacionPromedio
) {
}
