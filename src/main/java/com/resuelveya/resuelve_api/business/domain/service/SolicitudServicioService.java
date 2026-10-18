package com.resuelveya.resuelve_api.business.domain.service;

import com.resuelveya.resuelve_api.business.api.dto.solicitud.CrearSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.RechazarSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.SolicitudResponseDto;
import com.resuelveya.resuelve_api.business.data.entity.enums.EstadoSolicitud;

import java.util.List;

public interface SolicitudServicioService {

    SolicitudResponseDto crearSolicitud(String emailCliente, CrearSolicitudRequestDto requestDto);

    List<SolicitudResponseDto> listarSolicitudesCliente(String emailCliente, EstadoSolicitud estado);

    List<SolicitudResponseDto> listarSolicitudesTecnico(String emailTecnico, EstadoSolicitud estado);

    SolicitudResponseDto obtenerDetalle(String emailUsuario, Long solicitudId);

    SolicitudResponseDto aceptarSolicitud(String emailTecnico, Long solicitudId);

    SolicitudResponseDto rechazarSolicitud(String emailTecnico, Long solicitudId, RechazarSolicitudRequestDto requestDto);

    SolicitudResponseDto cancelarSolicitud(String emailCliente, Long solicitudId, RechazarSolicitudRequestDto requestDto);

    SolicitudResponseDto completarSolicitud(String emailUsuario, Long solicitudId);
}
