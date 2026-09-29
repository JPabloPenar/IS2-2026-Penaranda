package com.clubdeportivo.repository;

import com.clubdeportivo.model.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Acceso a datos de {@link Socio}. */
@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    /** Busca socios por nombre, apellido o DNI; con texto vacio devuelve todos, ordenados. */
    @Query("""
            SELECT s FROM Socio s
            WHERE LOWER(s.nombre)   LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(s.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR s.dni             LIKE CONCAT('%', :texto, '%')
            ORDER BY s.apellido, s.nombre
            """)
    List<Socio> buscar(@Param("texto") String texto);
}
