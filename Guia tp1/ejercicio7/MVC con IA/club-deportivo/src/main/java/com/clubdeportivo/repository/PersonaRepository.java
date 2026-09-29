package com.clubdeportivo.repository;

import com.clubdeportivo.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Consultas sobre TODAS las personas (socios y familiares). Es la puerta de entrada de la porteria:
 * el molinete busca por DNI y este metodo devuelve la subclase correcta (Socio o Familiar).
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /** Busqueda por DNI (columna con indice unico: O(log n)). */
    Optional<Persona> findByDni(String dni);

    /** El DNI es unico en todo el club, sea socio o familiar. */
    boolean existsByDni(String dni);
}
