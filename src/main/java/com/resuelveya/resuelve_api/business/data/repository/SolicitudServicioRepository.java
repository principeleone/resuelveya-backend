package com.resuelveya.resuelve_api.business.data.repository;

import com.resuelveya.resuelve_api.business.data.entity.SolicitudServicio;
import com.resuelveya.resuelve_api.business.data.entity.enums.EstadoSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolicitudServicioRepository extends JpaRepository<SolicitudServicio, Long> {

    List<SolicitudServicio> findByClienteIdOrderByCreatedAtDesc(Long clienteId);

    List<SolicitudServicio> findByClienteIdAndEstadoOrderByCreatedAtDesc(Long clienteId, EstadoSolicitud estado);

    List<SolicitudServicio> findByTecnicoIdOrderByCreatedAtDesc(Long tecnicoId);

    List<SolicitudServicio> findByTecnicoIdAndEstadoOrderByCreatedAtDesc(Long tecnicoId, EstadoSolicitud estado);

    long countByTecnicoIdAndEstado(Long tecnicoId, EstadoSolicitud estado);

    Optional<SolicitudServicio> findByIdAndClienteId(Long id, Long clienteId);

    Optional<SolicitudServicio> findByIdAndTecnicoId(Long id, Long tecnicoId);

    @Query("""
            SELECT s FROM SolicitudServicio s
            WHERE s.id = :id
              AND (s.cliente.id = :usuarioId OR s.tecnico.id = :usuarioId)
            """)
    Optional<SolicitudServicio> findByIdAndParticipante(@Param("id") Long id, @Param("usuarioId") Long usuarioId);
}
