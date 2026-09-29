package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Materia}. Los métodos CRUD heredados son suficientes. */
@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
}
