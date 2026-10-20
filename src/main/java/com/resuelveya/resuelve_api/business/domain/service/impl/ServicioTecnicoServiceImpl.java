package com.resuelveya.resuelve_api.business.domain.service.impl;

import com.resuelveya.resuelve_api.business.api.dto.servicio.ServicioRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.servicio.ServicioResponseDto;
import com.resuelveya.resuelve_api.business.api.dto.tecnico.TecnicoPerfilRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.usuario.PerfilResponseDto;
import com.resuelveya.resuelve_api.business.api.exception.RecursoNoEncontradoException;
import com.resuelveya.resuelve_api.business.data.entity.Categoria;
import com.resuelveya.resuelve_api.business.data.entity.Servicio;
import com.resuelveya.resuelve_api.business.data.entity.Tecnico;
import com.resuelveya.resuelve_api.business.data.repository.CategoriaRepository;
import com.resuelveya.resuelve_api.business.data.repository.ServicioRepository;
import com.resuelveya.resuelve_api.business.data.repository.TecnicoRepository;
import com.resuelveya.resuelve_api.business.data.repository.UsuarioRepository;
import com.resuelveya.resuelve_api.business.domain.mapper.ServicioMapper;
import com.resuelveya.resuelve_api.business.domain.service.ServicioTecnicoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ServicioTecnicoServiceImpl implements ServicioTecnicoService {

    private final TecnicoRepository tecnicoRepository;
    private final CategoriaRepository especialidadRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicioMapper servicioMapper;

    public ServicioTecnicoServiceImpl(
            TecnicoRepository tecnicoRepository,
            CategoriaRepository especialidadRepository,
            ServicioRepository servicioRepository,
            UsuarioRepository usuarioRepository,
            ServicioMapper servicioMapper) {
        this.tecnicoRepository = tecnicoRepository;
        this.especialidadRepository = especialidadRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.servicioMapper = servicioMapper;
    }

    @Override
    public PerfilResponseDto actualizarPerfilTecnico(String email, TecnicoPerfilRequestDto requestDto) {
        Tecnico tecnico = buscarTecnicoPorEmail(email);

        if (requestDto.presentacion() != null) {
            tecnico.setPresentacion(requestDto.presentacion().trim());
        }
        if (requestDto.aniosExperiencia() != null) {
            tecnico.setAniosExperiencia(requestDto.aniosExperiencia());
        }
        if (requestDto.especialidadId() != null) {
            Categoria especialidad = especialidadRepository.findById(requestDto.especialidadId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Especialidad no encontrada con id: " + requestDto.especialidadId()));
            tecnico.setEspecialidad(especialidad);
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
        if (requestDto.fotoUrl() != null) {
            tecnico.setFotoUrl(requestDto.fotoUrl().trim());
        }

        Tecnico guardado = tecnicoRepository.save(tecnico);

        return new PerfilResponseDto(
                guardado.getId(),
                guardado.getNombre(),
                guardado.getEmail(),
                guardado.getTelefono(),
                guardado.getFotoUrl(),
                guardado.getCiudad(),
                guardado.getCodigoUbigeo(),
                guardado.getRol(),
                guardado.getDireccion(),
                guardado.getLatitud(),
                guardado.getLongitud(),
                guardado.getCalificacionPromedio() != null ? guardado.getCalificacionPromedio() : 0.0,
                null, // puntosAcumulados (solo aplica a Cliente)
                guardado.getPresentacion(),
                guardado.getAniosExperiencia(),
                guardado.getYapeNumero(),
                guardado.getPlinNumero(),
                guardado.getTitularPago(),
                guardado.getEspecialidad() != null ? guardado.getEspecialidad().getId() : null,
                guardado.getEspecialidad() != null ? guardado.getEspecialidad().getNombre() : null,
                guardado.getValidacion() != null ? guardado.getValidacion() : false
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServicioResponseDto> listarMisServicios(String email) {
        return servicioRepository.findByTecnicoEmail(email)
                .stream()
                .map(servicioMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ServicioResponseDto obtenerMiServicioPorId(String email, Long servicioId) {
        Servicio servicio = servicioRepository.findByIdAndTecnicoEmail(servicioId, email)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Servicio no encontrado o no pertenece a tu cuenta"));
        return servicioMapper.toResponseDto(servicio);
    }

    @Override
    public ServicioResponseDto crearServicio(String email, ServicioRequestDto requestDto) {
        Tecnico tecnico = buscarTecnicoPorEmail(email);

        Categoria categoria = especialidadRepository.findById(requestDto.categoriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Categoría no encontrada con id: " + requestDto.categoriaId()));

        Servicio servicio = servicioMapper.toEntity(requestDto);
        servicio.setTecnico(tecnico);
        servicio.setCategoria(categoria);
        servicio.setActivo(requestDto.activo() != null ? requestDto.activo() : true);

        Servicio guardado = servicioRepository.save(servicio);
        return servicioMapper.toResponseDto(guardado);
    }

    @Override
    public ServicioResponseDto actualizarServicio(String email, Long servicioId, ServicioRequestDto requestDto) {
        Servicio servicio = servicioRepository.findByIdAndTecnicoEmail(servicioId, email)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Servicio no encontrado o no pertenece a tu cuenta"));

        if (!servicio.getCategoria().getId().equals(requestDto.categoriaId())) {
            Categoria nuevaCategoria = especialidadRepository.findById(requestDto.categoriaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Categoría no encontrada con id: " + requestDto.categoriaId()));
            servicio.setCategoria(nuevaCategoria);
        }

        servicioMapper.actualizarEntidad(requestDto, servicio);
        if (requestDto.activo() != null) {
            servicio.setActivo(requestDto.activo());
        }

        Servicio actualizado = servicioRepository.save(servicio);
        return servicioMapper.toResponseDto(actualizado);
    }

    @Override
    public void eliminarServicio(String email, Long servicioId) {
        Servicio servicio = servicioRepository.findByIdAndTecnicoEmail(servicioId, email)
                .orElseThrow(
                        () -> new RecursoNoEncontradoException("Servicio no encontrado o no pertenece a tu cuenta"));
        servicioRepository.delete(servicio);
    }

    private Tecnico buscarTecnicoPorEmail(String email) {
        return tecnicoRepository.findByEmail(email)
                .orElseGet(() -> {
                    var usuario = usuarioRepository.findByEmailIgnoreCase(email)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + email));
                    tecnicoRepository.registrarFilaTecnicoSiNoExiste(usuario.getId());
                    return tecnicoRepository.findById(usuario.getId())
                            .orElseThrow(() -> new RecursoNoEncontradoException("Perfil de técnico no encontrado con email: " + email));
                });
    }
}
