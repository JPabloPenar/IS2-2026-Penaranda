package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/** Acceso a datos de {@link Aula}. Los métodos CRUD heredados son suficientes. */
@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
}
