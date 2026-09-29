package com.clubdeportivo.repository;

import com.clubdeportivo.model.GrupoFamiliar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/** Acceso a datos de {@link GrupoFamiliar}. */
@Repository
public interface GrupoFamiliarRepository extends JpaRepository<GrupoFamiliar, Long> {

    /** Grupo que encabeza un socio (relacion 1:1 por titular_id). */
    Optional<GrupoFamiliar> findByTitularId(Long socioId);
}
