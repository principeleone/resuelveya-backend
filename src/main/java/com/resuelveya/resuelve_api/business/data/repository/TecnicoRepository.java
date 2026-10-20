package com.resuelveya.resuelve_api.business.data.repository;

import com.resuelveya.resuelve_api.business.data.entity.Tecnico;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {

    Optional<Tecnico> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    List<Tecnico> findByEspecialidadId(Long especialidadId);

    @Modifying
    @Query(value = "INSERT IGNORE INTO tecnico (usuario_id, anios_experiencia) VALUES (:usuarioId, 0)", nativeQuery = true)
    void registrarFilaTecnicoSiNoExiste(@Param("usuarioId") Long usuarioId);

    @Query("""
            SELECT t
            FROM Tecnico t
            WHERE (
                :especialidadId IS NULL
                OR t.especialidad.id = :especialidadId
            )
            AND (
                :nombre IS NULL
                OR LOWER(t.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))
            )
            """)
    Page<Tecnico> buscarTecnicos(
            @Param("especialidadId") Long especialidadId,
            @Param("nombre") String nombre,
            Pageable pageable
    );
}