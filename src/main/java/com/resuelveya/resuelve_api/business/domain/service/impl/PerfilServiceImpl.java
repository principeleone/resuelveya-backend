package com.resuelveya.resuelve_api.business.domain.service.impl;

import com.resuelveya.resuelve_api.business.api.dto.usuario.ActualizarPerfilRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.usuario.CambiarPasswordRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.usuario.PerfilResponseDto;
import com.resuelveya.resuelve_api.business.api.exception.OperacionNoPermitidaException;
import com.resuelveya.resuelve_api.business.api.exception.RecursoNoEncontradoException;
import com.resuelveya.resuelve_api.business.data.entity.Categoria;
import com.resuelveya.resuelve_api.business.data.entity.Cliente;
import com.resuelveya.resuelve_api.business.data.entity.Tecnico;
import com.resuelveya.resuelve_api.business.data.entity.Usuario;
import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import com.resuelveya.resuelve_api.business.data.repository.CategoriaRepository;
import com.resuelveya.resuelve_api.business.data.repository.ClienteRepository;
import com.resuelveya.resuelve_api.business.data.repository.TecnicoRepository;
import com.resuelveya.resuelve_api.business.data.repository.UsuarioRepository;
import com.resuelveya.resuelve_api.business.domain.service.PerfilService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PerfilServiceImpl implements PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaRepository categoriaRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilServiceImpl(
            UsuarioRepository usuarioRepository,
            TecnicoRepository tecnicoRepository,
            ClienteRepository clienteRepository,
            CategoriaRepository categoriaRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.clienteRepository = clienteRepository;
        this.categoriaRepository = categoriaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public PerfilResponseDto obtenerMiPerfil(String email) {
        Usuario usuario = buscarUsuarioPorEmail(email);
        return mapearAPerfilResponse(usuario);
    }

    @Override
    public PerfilResponseDto actualizarMiPerfil(String email, ActualizarPerfilRequestDto requestDto) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        usuario.setNombre(requestDto.nombre().trim());
        if (requestDto.telefono() != null) {
            usuario.setTelefono(requestDto.telefono().trim());
        }
        if (requestDto.ciudad() != null) {
            usuario.setCiudad(requestDto.ciudad().trim());
        }
        if (requestDto.codigoUbigeo() != null) {
            usuario.setCodigoUbigeo(requestDto.codigoUbigeo().trim());
        }
        if (requestDto.fotoUrl() != null) {
            usuario.setFotoUrl(requestDto.fotoUrl().trim());
        }
        if (requestDto.direccion() != null) {
            usuario.setDireccion(requestDto.direccion().trim());
        }
        if (requestDto.latitud() != null) {
            usuario.setLatitud(requestDto.latitud());
        }
        if (requestDto.longitud() != null) {
            usuario.setLongitud(requestDto.longitud());
        }

        Usuario actualizado = usuarioRepository.saveAndFlush(usuario);

        // Actualizar datos específicos de Cliente
        if (actualizado.getRol() == Rol.CLIENTE) {
            clienteRepository.registrarFilaClienteSiNoExiste(actualizado.getId());
        }

        // Actualizar datos específicos de Técnico
        if (actualizado.getRol() == Rol.TECNICO) {
            tecnicoRepository.registrarFilaTecnicoSiNoExiste(actualizado.getId());
            tecnicoRepository.findById(actualizado.getId()).ifPresent(tecnico -> {
                if (requestDto.presentacion() != null) {
                    tecnico.setPresentacion(requestDto.presentacion().trim());
                }
                if (requestDto.aniosExperiencia() != null) {
                    tecnico.setAniosExperiencia(requestDto.aniosExperiencia());
                }
                if (requestDto.especialidadId() != null) {
                    Categoria cat = categoriaRepository.findById(requestDto.especialidadId()).orElse(null);
                    tecnico.setEspecialidad(cat);
                }
                if (requestDto.yapeNumero() != null) {
                    tecnico.setYapeNumero(requestDto.yapeNumero().trim());
                }
                if (requestDto.plinNumero() != null) {
                    tecnico.setPlinNumero(requestDto.plinNumero().trim());
                }
                if (requestDto.titularPago() != null) {
                    tecnico.setTitularPago(requestDto.titularPago().trim());
                }
                tecnicoRepository.save(tecnico);
            });
        }

        return mapearAPerfilResponse(actualizado);
    }

    @Override
    public void cambiarPassword(String email, CambiarPasswordRequestDto requestDto) {
        Usuario usuario = buscarUsuarioPorEmail(email);

        if (!passwordEncoder.matches(requestDto.passwordActual(), usuario.getPassword())) {
            throw new OperacionNoPermitidaException("La contraseña actual ingresada es incorrecta.");
        }

        if (!requestDto.nuevaPassword().equals(requestDto.confirmarPassword())) {
            throw new OperacionNoPermitidaException("La nueva contraseña y su confirmación no coinciden.");
        }

        usuario.setPassword(passwordEncoder.encode(requestDto.nuevaPassword()));
        usuarioRepository.save(usuario);
    }

    private Usuario buscarUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
    }

    private PerfilResponseDto mapearAPerfilResponse(Usuario usuario) {
        if (usuario.getRol() == Rol.TECNICO) {
            return tecnicoRepository.findById(usuario.getId())
                    .map(t -> new PerfilResponseDto(
                            t.getId(),
                            t.getNombre(),
                            t.getEmail(),
                            t.getTelefono(),
                            t.getFotoUrl(),
                            t.getCiudad(),
                            t.getCodigoUbigeo(),
                            t.getRol(),
                            t.getDireccion(),
                            t.getLatitud(),
                            t.getLongitud(),
                            t.getCalificacionPromedio() != null ? t.getCalificacionPromedio() : 0.0,
                            null,
                            t.getPresentacion(),
                            t.getAniosExperiencia(),
                            t.getYapeNumero(),
                            t.getPlinNumero(),
                            t.getTitularPago(),
                            t.getEspecialidad() != null ? t.getEspecialidad().getId() : null,
                            t.getEspecialidad() != null ? t.getEspecialidad().getNombre() : null,
                            t.getValidacion() != null ? t.getValidacion() : false
                    ))
                    .orElseGet(() -> crearPerfilBase(usuario));
        }

        if (usuario.getRol() == Rol.CLIENTE) {
            return clienteRepository.findById(usuario.getId())
                    .map(c -> new PerfilResponseDto(
                            c.getId(),
                            c.getNombre(),
                            c.getEmail(),
                            c.getTelefono(),
                            c.getFotoUrl(),
                            c.getCiudad(),
                            c.getCodigoUbigeo(),
                            c.getRol(),
                            c.getDireccion(),
                            c.getLatitud(),
                            c.getLongitud(),
                            c.getCalificacionPromedio() != null ? c.getCalificacionPromedio() : 0.0,
                            c.getPuntosAcumulados() != null ? c.getPuntosAcumulados() : 0,
                            null, null, null, null, null, null, null, null
                    ))
                    .orElseGet(() -> crearPerfilBase(usuario));
        }

        return crearPerfilBase(usuario);
    }

    private PerfilResponseDto crearPerfilBase(Usuario usuario) {
        return new PerfilResponseDto(
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
                usuario.getCalificacionPromedio() != null ? usuario.getCalificacionPromedio() : 0.0,
                null, null, null, null, null, null, null, null, null
        );
    }
}
