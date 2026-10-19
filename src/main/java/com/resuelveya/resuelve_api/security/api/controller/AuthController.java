package com.resuelveya.resuelve_api.security.api.controller;

import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioRequestDTO;
import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioResponseDTO;
import com.resuelveya.resuelve_api.security.api.dto.FirebaseLoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginResponseDto;
import com.resuelveya.resuelve_api.security.domain.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponseDTO> registrar(
            @Valid
            @RequestBody
            UsuarioRequestDTO requestDto
    ) {
        UsuarioResponseDTO usuarioRegistrado = authService.registrar(requestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioRegistrado);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid
            @RequestBody
            LoginRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                authService.login(requestDto)
        );
    }

    @PostMapping("/firebase-login")
    public ResponseEntity<LoginResponseDto> firebaseLogin(
            @Valid
            @RequestBody
            FirebaseLoginRequestDto requestDto
    ) {
        return ResponseEntity.ok(
                authService.loginConFirebase(requestDto)
        );
    }
}

