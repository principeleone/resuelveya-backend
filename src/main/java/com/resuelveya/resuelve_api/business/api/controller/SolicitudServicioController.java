package com.resuelveya.resuelve_api.business.api.controller;

import com.resuelveya.resuelve_api.business.api.dto.solicitud.CrearSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.RechazarSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.SolicitudResponseDto;
import com.resuelveya.resuelve_api.business.data.entity.enums.EstadoSolicitud;
import com.resuelveya.resuelve_api.business.domain.service.SolicitudServicioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/solicitudes")
public class SolicitudServicioController {

    private final SolicitudServicioService solicitudServicioService;

    public SolicitudServicioController(SolicitudServicioService solicitudServicioService) {
        this.solicitudServicioService = solicitudServicioService;
    }

    @PostMapping
    public ResponseEntity<SolicitudResponseDto> crearSolicitud(
            Authentication authentication,
            @Valid @RequestBody CrearSolicitudRequestDto requestDto
    ) {
        String email = authentication.getName();
        SolicitudResponseDto creada = solicitudServicioService.crearSolicitud(email, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @GetMapping("/cliente")
    public ResponseEntity<List<SolicitudResponseDto>> listarSolicitudesCliente(
            Authentication authentication,
            @RequestParam(required = false) EstadoSolicitud estado
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.listarSolicitudesCliente(email, estado));
    }

    @GetMapping("/tecnico")
    public ResponseEntity<List<SolicitudResponseDto>> listarSolicitudesTecnico(
            Authentication authentication,
            @RequestParam(required = false) EstadoSolicitud estado
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.listarSolicitudesTecnico(email, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SolicitudResponseDto> obtenerDetalle(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.obtenerDetalle(email, id));
    }

    @PatchMapping("/{id}/aceptar")
    public ResponseEntity<SolicitudResponseDto> aceptarSolicitud(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.aceptarSolicitud(email, id));
    }

    @PatchMapping("/{id}/iniciar")
    public ResponseEntity<SolicitudResponseDto> iniciarSolicitud(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.iniciarSolicitud(email, id));
    }

    @PatchMapping("/{id}/rechazar")
    public ResponseEntity<SolicitudResponseDto> rechazarSolicitud(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody(required = false) RechazarSolicitudRequestDto requestDto
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.rechazarSolicitud(email, id, requestDto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<SolicitudResponseDto> cancelarSolicitud(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody(required = false) RechazarSolicitudRequestDto requestDto
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.cancelarSolicitud(email, id, requestDto));
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<SolicitudResponseDto> completarSolicitud(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(solicitudServicioService.completarSolicitud(email, id));
    }
}
