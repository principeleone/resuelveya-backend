package com.resuelveya.resuelve_api.security.domain.service.impl;

import com.google.firebase.auth.FirebaseToken;
import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioRequestDTO;
import com.resuelveya.resuelve_api.business.api.dto.usuario.UsuarioResponseDTO;
import com.resuelveya.resuelve_api.business.api.exception.RecursoDuplicadoException;
import com.resuelveya.resuelve_api.business.data.entity.Cliente;
import com.resuelveya.resuelve_api.business.data.entity.Tecnico;
import com.resuelveya.resuelve_api.business.data.entity.Usuario;
import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import com.resuelveya.resuelve_api.business.data.repository.ClienteRepository;
import com.resuelveya.resuelve_api.business.data.repository.TecnicoRepository;
import com.resuelveya.resuelve_api.business.data.repository.UsuarioRepository;
import com.resuelveya.resuelve_api.security.api.dto.FirebaseLoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginRequestDto;
import com.resuelveya.resuelve_api.security.api.dto.LoginResponseDto;
import com.resuelveya.resuelve_api.security.domain.service.AuthService;
import com.resuelveya.resuelve_api.security.domain.service.CustomUserDetailsService;
import com.resuelveya.resuelve_api.security.domain.service.FirebaseService;
import com.resuelveya.resuelve_api.security.domain.service.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final FirebaseService firebaseService;
    private final CustomUserDetailsService customUserDetailsService;

    public AuthServiceImpl(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            TecnicoRepository tecnicoRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            FirebaseService firebaseService,
            CustomUserDetailsService customUserDetailsService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.firebaseService = firebaseService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    public UsuarioResponseDTO registrar(UsuarioRequestDTO requestDTO) {
        String username = normalizarEmail(requestDTO.email());

        if (usuarioRepository.existsByEmailIgnoreCase(username)) {
            throw new RecursoDuplicadoException("El correo ya está registrado");
        }

        String encodedPassword = passwordEncoder.encode(requestDTO.password());
        String nombre = requestDTO.nombre().trim();
        String telefono = requestDTO.telefono();
        Rol rol = requestDTO.rol() != null ? requestDTO.rol() : Rol.CLIENTE;

        Usuario usuarioGuardado;

        switch (rol) {
            case TECNICO -> {
                Tecnico tecnico = new Tecnico();
                tecnico.setNombre(nombre);
                tecnico.setEmail(username);
                tecnico.setPassword(encodedPassword);
                tecnico.setTelefono(telefono);
                tecnico.setRol(Rol.TECNICO);
                tecnico.setAniosExperiencia(0);
                tecnico.setCalificacionPromedio(0.0);
                tecnico.setPresentacion(null);
                tecnico.setYapeNumero(null);
                tecnico.setPlinNumero(null);
                tecnico.setTitularPago(null);
                tecnico.setEspecialidad(null);
                usuarioGuardado = tecnicoRepository.saveAndFlush(tecnico);
                tecnicoRepository.registrarFilaTecnicoSiNoExiste(usuarioGuardado.getId());
            }
            case CLIENTE -> {
                Cliente cliente = new Cliente();
                cliente.setNombre(nombre);
                cliente.setEmail(username);
                cliente.setPassword(encodedPassword);
                cliente.setTelefono(telefono);
                cliente.setRol(Rol.CLIENTE);
                cliente.setDireccion(null);
                usuarioGuardado = clienteRepository.saveAndFlush(cliente);
                clienteRepository.registrarFilaClienteSiNoExiste(usuarioGuardado.getId());
            }
            default -> {
                Usuario admin = new Usuario();
                admin.setNombre(nombre);
                admin.setEmail(username);
                admin.setPassword(encodedPassword);
                admin.setTelefono(telefono);
                admin.setRol(Rol.ADMIN);
                usuarioGuardado = usuarioRepository.saveAndFlush(admin);
            }
        }

        return convertirAResponse(usuarioGuardado);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto requestDto) {
        String username = normalizarEmail(requestDto.email());
        UsernamePasswordAuthenticationToken solicitud = new UsernamePasswordAuthenticationToken(username, requestDto.password());

        UserDetails userDetails = (UserDetails) authenticationManager.authenticate(solicitud).getPrincipal();

        String token = jwtService.generarToken(userDetails);
        String authority = userDetails.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_CLIENTE");

        Rol rol = Rol.valueOf(authority.replace("ROLE_", ""));

        // Recuperar datos complementarios del usuario para el frontend
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(username).orElse(null);
        Long id = usuario != null ? usuario.getId() : null;
        String nombre = usuario != null ? usuario.getNombre() : null;
        String telefono = usuario != null ? usuario.getTelefono() : null;

        return new LoginResponseDto(
                token, "Bearer", jwtService.obtenerTiempoExpiracion(),
                userDetails.getUsername(), rol,
                id, nombre, telefono
        );
    }

    @Override
    public LoginResponseDto loginConFirebase(FirebaseLoginRequestDto requestDto) {
        FirebaseToken decodedToken = firebaseService.verifyIdToken(requestDto.firebaseToken());

        String email = normalizarEmail(decodedToken.getEmail());
        String nombre = requestDto.nombre() != null && !requestDto.nombre().isBlank()
                ? requestDto.nombre().trim()
                : (decodedToken.getName() != null ? decodedToken.getName() : email);
        String fotoUrl = decodedToken.getPicture();

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> registrarUsuarioDesdeFirebase(email, nombre, fotoUrl, requestDto));

        // Actualizar datos si el usuario ya existía pero envía datos complementarios nuevos
        boolean modificado = false;
        if (fotoUrl != null && (usuario.getFotoUrl() == null || usuario.getFotoUrl().isBlank())) {
            usuario.setFotoUrl(fotoUrl);
            modificado = true;
        }
        if (requestDto.nombre() != null && !requestDto.nombre().isBlank() && (usuario.getNombre() == null || usuario.getNombre().isBlank() || usuario.getNombre().equals(email))) {
            usuario.setNombre(requestDto.nombre().trim());
            modificado = true;
        }
        if (requestDto.telefono() != null && !requestDto.telefono().isBlank() && (usuario.getTelefono() == null || usuario.getTelefono().isBlank())) {
            usuario.setTelefono(requestDto.telefono().trim());
            modificado = true;
        }
        if (requestDto.ciudad() != null && !requestDto.ciudad().isBlank() && (usuario.getCiudad() == null || usuario.getCiudad().isBlank())) {
            usuario.setCiudad(requestDto.ciudad().trim());
            modificado = true;
        }
        if (requestDto.direccion() != null && !requestDto.direccion().isBlank()) {
            if (usuario.getDireccion() == null || usuario.getDireccion().isBlank()) {
                usuario.setDireccion(requestDto.direccion().trim());
                modificado = true;
            }
        }
        if (requestDto.latitud() != null && usuario.getLatitud() == null) {
            usuario.setLatitud(requestDto.latitud());
            modificado = true;
        }
        if (requestDto.longitud() != null && usuario.getLongitud() == null) {
            usuario.setLongitud(requestDto.longitud());
            modificado = true;
        }

        if (modificado) {
            usuarioRepository.save(usuario);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(usuario.getEmail());
        String token = jwtService.generarToken(userDetails);

        return new LoginResponseDto(
                token, "Bearer", jwtService.obtenerTiempoExpiracion(),
                usuario.getEmail(), usuario.getRol(),
                usuario.getId(), usuario.getNombre(), usuario.getTelefono()
        );
    }

    private Usuario registrarUsuarioDesdeFirebase(String email, String nombre, String fotoUrl, FirebaseLoginRequestDto requestDto) {
        Rol rol = requestDto.rol() != null ? requestDto.rol() : Rol.CLIENTE;
        String placeholderPassword = passwordEncoder.encode(UUID.randomUUID().toString());
        String telefono = requestDto.telefono() != null && !requestDto.telefono().isBlank() ? requestDto.telefono().trim() : null;
        String ciudad = requestDto.ciudad() != null && !requestDto.ciudad().isBlank() ? requestDto.ciudad().trim() : null;
        String direccion = requestDto.direccion() != null && !requestDto.direccion().isBlank() ? requestDto.direccion().trim() : null;
        Double latitud = requestDto.latitud();
        Double longitud = requestDto.longitud();

        return switch (rol) {
            case TECNICO -> {
                Tecnico tecnico = new Tecnico();
                tecnico.setNombre(nombre);
                tecnico.setEmail(email);
                tecnico.setPassword(placeholderPassword);
                tecnico.setRol(Rol.TECNICO);
                tecnico.setTelefono(telefono);
                tecnico.setCiudad(ciudad);
                tecnico.setDireccion(direccion);
                tecnico.setLatitud(latitud);
                tecnico.setLongitud(longitud);
                tecnico.setFotoUrl(fotoUrl);
                tecnico.setAniosExperiencia(0);
                tecnico.setCalificacionPromedio(0.0);
                Tecnico guardado = tecnicoRepository.saveAndFlush(tecnico);
                tecnicoRepository.registrarFilaTecnicoSiNoExiste(guardado.getId());
                yield guardado;
            }
            case CLIENTE -> {
                Cliente cliente = new Cliente();
                cliente.setNombre(nombre);
                cliente.setEmail(email);
                cliente.setPassword(placeholderPassword);
                cliente.setRol(Rol.CLIENTE);
                cliente.setTelefono(telefono);
                cliente.setCiudad(ciudad);
                cliente.setDireccion(direccion);
                cliente.setLatitud(latitud);
                cliente.setLongitud(longitud);
                cliente.setFotoUrl(fotoUrl);
                cliente.setPuntosAcumulados(0);
                cliente.setCalificacionPromedio(0.0);
                Cliente guardado = clienteRepository.saveAndFlush(cliente);
                clienteRepository.registrarFilaClienteSiNoExiste(guardado.getId());
                yield guardado;
            }
            default -> {
                Usuario admin = new Usuario();
                admin.setNombre(nombre);
                admin.setEmail(email);
                admin.setPassword(placeholderPassword);
                admin.setRol(Rol.ADMIN);
                admin.setTelefono(telefono);
                admin.setCiudad(ciudad);
                admin.setFotoUrl(fotoUrl);
                yield usuarioRepository.saveAndFlush(admin);
            }
        };
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase();
    }

    private UsuarioResponseDTO convertirAResponse(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getFotoUrl(),
                usuario.getCiudad(),
                usuario.getCodigoUbigeo(),
                usuario.getRol(),
                usuario.getDireccion(),
                usuario.getLatitud(),
                usuario.getLongitud(),
                usuario.getCalificacionPromedio()
        );
    }
}
