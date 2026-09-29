package com.clubdeportivo.repository;

import com.clubdeportivo.model.RegistroAcceso;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de {@link RegistroAcceso}. */
@Repository
public interface RegistroAccesoRepository extends JpaRepository<RegistroAcceso, Long> {

    /**
     * Ultimo pasaje PERMITIDO de una persona (los rechazados no cambian si esta adentro o afuera).
     * Usa el indice (persona_id, fecha_hora).
     */
    Optional<RegistroAcceso> findFirstByPersonaIdAndPermitidoTrueOrderByFechaHoraDesc(Long personaId);

    /** Los 10 intentos mas recientes de toda la porteria, con la persona cargada en la misma consulta. */
    @EntityGraph(attributePaths = "persona")
    List<RegistroAcceso> findTop10ByOrderByFechaHoraDesc();
}
