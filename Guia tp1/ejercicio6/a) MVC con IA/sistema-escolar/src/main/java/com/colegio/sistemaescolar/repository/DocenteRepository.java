package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Docente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acceso a datos de {@link Docente}.
 *
 * <p>{@code JpaRepository<Docente, Long>} entrega gratis save, findById, findAll, delete, count...
 * Los métodos "derivados" (findByEmail, existsByEmail) los implementa Spring Data a partir del nombre.
 */
@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    /** SELECT * FROM docentes WHERE email = ? (lo usa el login). */
    Optional<Docente> findByEmail(String email);

    /** ¿Ya existe un docente con ese email? (evita duplicados en el registro). */
    boolean existsByEmail(String email);
}
