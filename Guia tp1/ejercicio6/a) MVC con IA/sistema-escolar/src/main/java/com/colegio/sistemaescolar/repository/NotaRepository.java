package com.colegio.sistemaescolar.repository;

import com.colegio.sistemaescolar.model.Nota;
import com.colegio.sistemaescolar.model.Periodo;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Acceso a datos de {@link Nota}. */
@Repository
public interface NotaRepository extends JpaRepository<Nota, Long> {

    /**
     * Lista notas aplicando filtros opcionales (alumno, materia, período).
     * Un parámetro null significa "sin filtro". Se cargan alumno, materia y docente con JOIN FETCH.
     */
    @Query("""
            SELECT n FROM Nota n
              JOIN FETCH n.alumno a
              JOIN FETCH n.materia m
              LEFT JOIN FETCH n.docente d
            WHERE (:alumnoId IS NULL OR a.id = :alumnoId)
              AND (:materiaId IS NULL OR m.id = :materiaId)
              AND (:periodo IS NULL OR n.periodo = :periodo)
            ORDER BY a.apellido, a.nombre, m.nombre, n.periodo
            """)
    List<Nota> filtrar(@Param("alumnoId") Long alumnoId,
                       @Param("materiaId") Long materiaId,
                       @Param("periodo") Periodo periodo);

    /** Para evitar duplicados: una sola nota por alumno + materia + período. */
    Optional<Nota> findByAlumnoIdAndMateriaIdAndPeriodo(Long alumnoId, Long materiaId, Periodo periodo);

    /** Las 5 notas registradas más recientemente (panel principal). */
    @EntityGraph(attributePaths = {"alumno", "materia", "docente"})
    List<Nota> findTop5ByOrderByFechaCreacionDesc();

    /** Promedio general de todas las notas; null si aún no hay notas. */
    @Query("SELECT AVG(n.valorNumerico) FROM Nota n")
    Double promedioGeneral();
}
