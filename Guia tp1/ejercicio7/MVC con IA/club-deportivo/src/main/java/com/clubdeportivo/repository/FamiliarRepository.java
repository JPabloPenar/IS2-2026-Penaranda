package com.clubdeportivo.repository;

import com.clubdeportivo.model.Familiar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Acceso a datos de {@link Familiar}. */
@Repository
public interface FamiliarRepository extends JpaRepository<Familiar, Long> {

    /** Familiares de un grupo, ordenados alfabeticamente. */
    List<Familiar> findByGrupoFamiliarIdOrderByApellidoAscNombreAsc(Long grupoFamiliarId);
}
