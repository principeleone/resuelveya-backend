package com.resuelveya.resuelve_api.business.domain.service.impl;

import com.resuelveya.resuelve_api.business.api.dto.cliente.ClienteRequestDto;
import com.resuelveya.resuelve_api.business.api.dto.cliente.ClienteResponseDto;
import com.resuelveya.resuelve_api.business.api.exception.RecursoNoEncontradoException;
import com.resuelveya.resuelve_api.business.data.entity.Cliente;
import com.resuelveya.resuelve_api.business.data.entity.enums.Rol;
import com.resuelveya.resuelve_api.business.data.repository.ClienteRepository;
import com.resuelveya.resuelve_api.business.domain.mapper.ClienteMapper;
import com.resuelveya.resuelve_api.business.domain.service.ClienteService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    @PersistenceContext
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponseDto> obtenerTodos() {
        return clienteMapper.toResponseDtoList(clienteRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponseDto obtenerPorId(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));
        return clienteMapper.toResponseDto(cliente);
    }

    @Override
    @Transactional
    public ClienteResponseDto crear(ClienteRequestDto requestDto) {
        Cliente cliente = clienteMapper.toEntity(requestDto);
        cliente.setRol(Rol.CLIENTE);
        if (cliente.getPuntosAcumulados() == null) {
            cliente.setPuntosAcumulados(0);
        }
        if (cliente.getCalificacionPromedio() == null) {
            cliente.setCalificacionPromedio(0.0);
        }

        // Requerimiento T1: Uso de saveAndFlush para sincronización inmediata con la BD
        Cliente guardado = clienteRepository.saveAndFlush(cliente);
        return clienteMapper.toResponseDto(guardado);
    }

    @Override
    @Transactional
    public ClienteResponseDto actualizar(Long id, ClienteRequestDto requestDto) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id));

        clienteMapper.updateEntityFromDto(requestDto, cliente);

        // Requerimiento T1: Sincronización explícita al actualizar
        Cliente actualizado = clienteRepository.saveAndFlush(cliente);
        return clienteMapper.toResponseDto(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Cliente no encontrado con ID: " + id);
        }
        clienteRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void procesamientoMasivoClientes(List<ClienteRequestDto> listaClientes) {
        // Requerimiento T1: Control manual de Flush y vaciado de cache Nivel 1 en procesos masivos
        for (int i = 0; i < listaClientes.size(); i++) {
            Cliente cliente = clienteMapper.toEntity(listaClientes.get(i));
            cliente.setRol(Rol.CLIENTE);
            clienteRepository.save(cliente);

            if (i > 0 && i % 30 == 0) {
                entityManager.flush(); // Fuerza el envío del SQL a la base de datos
                entityManager.clear(); // Limpia la memoria cache de JPA
            }
        }
    }
}