package com.resuelveya.resuelve_api.security.domain.service;

import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioRequestDTO;
import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioResponseDTO;
import com.resuelveya.resuelve_api.security.api.dto.FirebaseLoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.FirebaseRegisterRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginResponseDto;

public interface AuthService {
    UsuarioResponseDTO registrar(
            UsuarioRequestDTO requestDTO
    );

    UsuarioResponseDTO registrarConFirebase(
            FirebaseRegisterRequestDto requestDto
    );

    LoginResponseDto login(
            LoginRequestDto requestDto
    );

    LoginResponseDto loginConFirebase(
            FirebaseLoginRequestDto requestDto
    );
}

