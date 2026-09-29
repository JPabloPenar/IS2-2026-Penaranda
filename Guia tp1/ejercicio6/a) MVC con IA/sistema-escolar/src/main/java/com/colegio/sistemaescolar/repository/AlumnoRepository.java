package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/** Acceso a datos de {@link Alumno}. */
@Repository
public interface AlumnoRepository extends JpaRepository<Alumno, Long> {

    boolean existsByDni(String dni);

    /** Para editar: ¿otro alumno (distinto del actual) ya usa este DNI? */
    boolean existsByDniAndIdNot(String dni, Long id);

    /** Cantidad de alumnos asignados a un aula (para controlar su capacidad). */
    long countByAulaId(Long aulaId);

    /**
     * Búsqueda por nombre, apellido o DNI. Con texto vacío devuelve todos.
     * {@code JOIN FETCH} carga grado y aula en la MISMA consulta y evita el problema N+1.
     */
    @Query("""
            SELECT a FROM Alumno a
              LEFT JOIN FETCH a.grado
              LEFT JOIN FETCH a.aula
            WHERE LOWER(a.nombre)   LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(a.apellido) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR a.dni             LIKE CONCAT('%', :texto, '%')
            ORDER BY a.apellido, a.nombre
            """)
    List<Alumno> buscar(@Param("texto") String texto);
}
