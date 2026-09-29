package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Grado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Grado}. Los métodos CRUD heredados son suficientes. */
@Repository
public interface GradoRepository extends JpaRepository<Grado, Long> {
}
