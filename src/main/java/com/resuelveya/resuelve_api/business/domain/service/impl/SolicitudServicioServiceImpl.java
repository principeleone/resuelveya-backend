package com.resuelveya.resuelve_api.business.domain.service.impl;

import com.resuelveya.resuelve_api.business.api.dto.solicitud.CrearSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.RechazarSolicitudRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.solicitud.SolicitudResponseDto;
import com.resuelveya.resuelve_api.business.api.exception.OperacionNoPermitidaException;
import com.resuelveya.resuelve_api.business.api.exception.RecursoNoEncontradoException;
import com.resuelveya.resuelve_api.business.data.entity.Cliente;
import com.resuelveya.resuelve_api.business.data.entity.Servicio;
import com.resuelveya.resuelve_api.business.data.entity.SolicitudServicio;
import com.resuelveya.resuelve_api.business.data.entity.Tecnico;
import com.resuelveya.resuelve_api.business.data.entity.Usuario;
import com.resuelveya.resuelve_api.business.data.entity.enums.EstadoSolicitud;
import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import com.resuelveya.resuelve_api.business.data.repository.ClienteRepository;
import com.resuelveya.resuelve_api.business.data.repository.ServicioRepository;
import com.resuelveya.resuelve_api.business.data.repository.SolicitudServicioRepository;
import com.resuelveya.resuelve_api.business.data.repository.TecnicoRepository;
import com.resuelveya.resuelve_api.business.data.repository.UsuarioRepository;
import com.resuelveya.resuelve_api.business.domain.mapper.SolicitudServicioMapper;
import com.resuelveya.resuelve_api.business.domain.service.SolicitudServicioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SolicitudServicioServiceImpl implements SolicitudServicioService {

    private final SolicitudServicioRepository solicitudServicioRepository;
    private final ClienteRepository clienteRepository;
    private final TecnicoRepository tecnicoRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final SolicitudServicioMapper solicitudServicioMapper;

    public SolicitudServicioServiceImpl(
            SolicitudServicioRepository solicitudServicioRepository,
            ClienteRepository clienteRepository,
            TecnicoRepository tecnicoRepository,
            ServicioRepository servicioRepository,
            UsuarioRepository usuarioRepository,
            SolicitudServicioMapper solicitudServicioMapper
    ) {
        this.solicitudServicioRepository = solicitudServicioRepository;
        this.clienteRepository = clienteRepository;
        this.tecnicoRepository = tecnicoRepository;
        this.servicioRepository = servicioRepository;
        this.usuarioRepository = usuarioRepository;
        this.solicitudServicioMapper = solicitudServicioMapper;
    }

    private Cliente obtenerOCrearCliente(String emailCliente) {
        return clienteRepository.findByEmail(emailCliente)
                .orElseGet(() -> {
                    Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailCliente)
                            .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + emailCliente));
                    clienteRepository.registrarFilaClienteSiNoExiste(usuario.getId());
                    return clienteRepository.findById(usuario.getId())
                            .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con email: " + emailCliente));
                });
    }

    private Tecnico obtenerTecnico(String emailTecnico) {
        return tecnicoRepository.findByEmail(emailTecnico)
                .orElseThrow(() -> new RecursoNoEncontradoException("Técnico no encontrado con email: " + emailTecnico));
    }

    private SolicitudServicio obtenerSolicitudPorId(Long solicitudId) {
        return solicitudServicioRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud no encontrada con ID: " + solicitudId));
    }

    @Override
    public SolicitudResponseDto crearSolicitud(String emailCliente, CrearSolicitudRequestDto requestDto) {
        Cliente cliente = obtenerOCrearCliente(emailCliente);

        Servicio servicio = null;
        Tecnico tecnico = null;

        if (requestDto.servicioId() != null) {
            servicio = servicioRepository.findById(requestDto.servicioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Servicio no encontrado con ID: " + requestDto.servicioId()));

            if (Boolean.FALSE.equals(servicio.getActivo())) {
                throw new OperacionNoPermitidaException("El servicio solicitado no se encuentra activo.");
            }

            tecnico = servicio.getTecnico();
        } else if (requestDto.tecnicoId() != null) {
            tecnico = tecnicoRepository.findById(requestDto.tecnicoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Técnico no encontrado con ID: " + requestDto.tecnicoId()));
        } else {
            throw new OperacionNoPermitidaException("Debe especificar al menos un servicio o un técnico para realizar la solicitud.");
        }

        if (tecnico.getId().equals(cliente.getId())) {
            throw new OperacionNoPermitidaException("No puedes solicitarte un servicio a ti mismo.");
        }

        SolicitudServicio solicitud = solicitudServicioMapper.toEntity(requestDto);
        solicitud.setCliente(cliente);
        solicitud.setTecnico(tecnico);
        solicitud.setServicio(servicio);
        solicitud.setEstado(EstadoSolicitud.PENDIENTE);

        if (solicitud.getPrecioAcordado() == null && servicio != null) {
            solicitud.setPrecioAcordado(servicio.getPrecioEstimado());
        }

        SolicitudServicio guardada = solicitudServicioRepository.save(solicitud);
        return solicitudServicioMapper.toResponseDto(guardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDto> listarSolicitudesCliente(String emailCliente, EstadoSolicitud estado) {
        Cliente cliente = obtenerOCrearCliente(emailCliente);

        List<SolicitudServicio> lista = (estado != null)
                ? solicitudServicioRepository.findByClienteIdAndEstadoOrderByCreatedAtDesc(cliente.getId(), estado)
                : solicitudServicioRepository.findByClienteIdOrderByCreatedAtDesc(cliente.getId());

        return lista.stream()
                .map(solicitudServicioMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<SolicitudResponseDto> listarSolicitudesTecnico(String emailTecnico, EstadoSolicitud estado) {
        Tecnico tecnico = obtenerTecnico(emailTecnico);

        List<SolicitudServicio> lista = (estado != null)
                ? solicitudServicioRepository.findByTecnicoIdAndEstadoOrderByCreatedAtDesc(tecnico.getId(), estado)
                : solicitudServicioRepository.findByTecnicoIdOrderByCreatedAtDesc(tecnico.getId());

        return lista.stream()
                .map(solicitudServicioMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SolicitudResponseDto obtenerDetalle(String emailUsuario, Long solicitudId) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + emailUsuario));

        SolicitudServicio solicitud = obtenerSolicitudPorId(solicitudId);

        boolean esCliente = solicitud.getCliente().getId().equals(usuario.getId());
        boolean esTecnico = solicitud.getTecnico().getId().equals(usuario.getId());
        boolean esAdmin = Rol.ADMIN.equals(usuario.getRol());

        if (!esCliente && !esTecnico && !esAdmin) {
            throw new OperacionNoPermitidaException("No tienes permisos para visualizar esta solicitud.");
        }

        return solicitudServicioMapper.toResponseDto(solicitud);
    }

    @Override
    public SolicitudResponseDto aceptarSolicitud(String emailTecnico, Long solicitudId) {
        Tecnico tecnico = obtenerTecnico(emailTecnico);
        SolicitudServicio solicitud = obtenerSolicitudPorId(solicitudId);

        if (!solicitud.getTecnico().getId().equals(tecnico.getId())) {
            throw new OperacionNoPermitidaException("No tienes permisos para aceptar esta solicitud.");
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new OperacionNoPermitidaException("Solo se pueden aceptar solicitudes en estado PENDIENTE. Estado actual: " + solicitud.getEstado());
        }

        solicitud.setEstado(EstadoSolicitud.ACEPTADA);
        SolicitudServicio actualizada = solicitudServicioRepository.save(solicitud);
        return solicitudServicioMapper.toResponseDto(actualizada);
    }

    @Override
    public SolicitudResponseDto rechazarSolicitud(String emailTecnico, Long solicitudId, RechazarSolicitudRequestDto requestDto) {
        Tecnico tecnico = obtenerTecnico(emailTecnico);
        SolicitudServicio solicitud = obtenerSolicitudPorId(solicitudId);

        if (!solicitud.getTecnico().getId().equals(tecnico.getId())) {
            throw new OperacionNoPermitidaException("No tienes permisos para rechazar esta solicitud.");
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
            throw new OperacionNoPermitidaException("Solo se pueden rechazar solicitudes en estado PENDIENTE. Estado actual: " + solicitud.getEstado());
        }

        solicitud.setEstado(EstadoSolicitud.RECHAZADA);
        if (requestDto != null && requestDto.motivo() != null && !requestDto.motivo().isBlank()) {
            solicitud.setMotivoCancelacion(requestDto.motivo());
        }

        SolicitudServicio actualizada = solicitudServicioRepository.save(solicitud);
        return solicitudServicioMapper.toResponseDto(actualizada);
    }

    @Override
    public SolicitudResponseDto cancelarSolicitud(String emailCliente, Long solicitudId, RechazarSolicitudRequestDto requestDto) {
        Cliente cliente = obtenerOCrearCliente(emailCliente);
        SolicitudServicio solicitud = obtenerSolicitudPorId(solicitudId);

        if (!solicitud.getCliente().getId().equals(cliente.getId())) {
            throw new OperacionNoPermitidaException("No tienes permisos para cancelar esta solicitud.");
        }

        if (solicitud.getEstado() != EstadoSolicitud.PENDIENTE && solicitud.getEstado() != EstadoSolicitud.ACEPTADA) {
            throw new OperacionNoPermitidaException("No se puede cancelar una solicitud en estado: " + solicitud.getEstado());
        }

        solicitud.setEstado(EstadoSolicitud.CANCELADA);
        if (requestDto != null && requestDto.motivo() != null && !requestDto.motivo().isBlank()) {
            solicitud.setMotivoCancelacion(requestDto.motivo());
        }

        SolicitudServicio actualizada = solicitudServicioRepository.save(solicitud);
        return solicitudServicioMapper.toResponseDto(actualizada);
    }

    @Override
    public SolicitudResponseDto completarSolicitud(String emailUsuario, Long solicitudId) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(emailUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con email: " + emailUsuario));

        SolicitudServicio solicitud = obtenerSolicitudPorId(solicitudId);

        boolean esCliente = solicitud.getCliente().getId().equals(usuario.getId());
        boolean esTecnico = solicitud.getTecnico().getId().equals(usuario.getId());

        if (!esCliente && !esTecnico) {
            throw new OperacionNoPermitidaException("Solo las partes involucradas pueden marcar la solicitud como completada.");
        }

        if (solicitud.getEstado() != EstadoSolicitud.ACEPTADA) {
            throw new OperacionNoPermitidaException("Solo se puede completar una solicitud que esté ACEPTADA. Estado actual: " + solicitud.getEstado());
        }

        solicitud.setEstado(EstadoSolicitud.COMPLETADA);
        SolicitudServicio actualizada = solicitudServicioRepository.save(solicitud);
        return solicitudServicioMapper.toResponseDto(actualizada);
    }
}
