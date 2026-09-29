package com.clubdeportivo.repository;

import com.clubdeportivo.model.Cuota;
import com.clubdeportivo.model.EstadoCuota;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/** Acceso a datos de {@link Cuota}: consultas de "cuota al dia", listados y bloqueo para cobrar. */
@Repository
public interface CuotaRepository extends JpaRepository<Cuota, Long> {

    boolean existsByGrupoFamiliarIdAndMesPeriodoAndAnioPeriodo(Long grupoFamiliarId, int mes, int anio);

    /**
     * Cantidad de cuotas ADEUDADAS de un grupo: no pagadas y con vencimiento anterior a hoy.
     * Un grupo esta "al dia" cuando el resultado es 0. Se calcula por fecha (no por el campo estado)
     * para que sea correcto aunque la tarea nocturna de vencimientos aun no haya corrido.
     * Se ejecuta en CADA ingreso, por eso usa el indice (estado_cuota, fecha_vencimiento).
     */
    @Query("""
            SELECT COUNT(c) FROM Cuota c
            WHERE c.grupoFamiliar.id = :grupoId
              AND c.estadoCuota <> com.clubdeportivo.model.EstadoCuota.PAGADA
              AND c.fechaVencimiento < :hoy
            """)
    long contarAdeudadas(@Param("grupoId") Long grupoId, @Param("hoy") LocalDate hoy);

    /** Ids de todos los grupos con deuda: permite marcar "al dia" en un listado con UNA sola consulta. */
    @Query("""
            SELECT DISTINCT c.grupoFamiliar.id FROM Cuota c
            WHERE c.estadoCuota <> com.clubdeportivo.model.EstadoCuota.PAGADA
              AND c.fechaVencimiento < :hoy
            """)
    List<Long> gruposConDeuda(@Param("hoy") LocalDate hoy);

    /**
     * Carga la cuota con BLOQUEO PESIMISTA (SELECT ... FOR UPDATE). Dos cobros simultaneos de la misma
     * cuota se ejecutan uno despues del otro, evitando pagar de mas o dejar el estado inconsistente.
     * Debe llamarse dentro de una transaccion.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Cuota c WHERE c.id = :id")
    Optional<Cuota> buscarParaActualizar(@Param("id") Long id);

    /** Cuota con grupo, titular y pagos en una sola consulta (para mostrar en el formulario de cobro). */
    @Query("""
            SELECT DISTINCT c FROM Cuota c
              JOIN FETCH c.grupoFamiliar g
              JOIN FETCH g.titular
              LEFT JOIN FETCH c.pagos
            WHERE c.id = :id
            """)
    Optional<Cuota> buscarConDetalle(@Param("id") Long id);

    /** Cuotas de un grupo, de la mas reciente a la mas antigua. */
    @Query("""
            SELECT DISTINCT c FROM Cuota c
              JOIN FETCH c.grupoFamiliar g
              JOIN FETCH g.titular
              LEFT JOIN FETCH c.pagos
            WHERE g.id = :grupoId
            ORDER BY c.anioPeriodo DESC, c.mesPeriodo DESC
            """)
    List<Cuota> listarPorGrupo(@Param("grupoId") Long grupoId);

    /** Listado general con filtro opcional por estado (null = todos). */
    @Query("""
            SELECT DISTINCT c FROM Cuota c
              JOIN FETCH c.grupoFamiliar g
              JOIN FETCH g.titular
              LEFT JOIN FETCH c.pagos
            WHERE (:estado IS NULL OR c.estadoCuota = :estado)
            ORDER BY c.anioPeriodo DESC, c.mesPeriodo DESC
            """)
    List<Cuota> listarConPagos(@Param("estado") EstadoCuota estado);

    /**
     * Marca como VENCIDA toda cuota PENDIENTE cuyo vencimiento ya paso (actualizacion masiva en una
     * sola sentencia UPDATE). {@code @Modifying} indica que no es un SELECT; {@code clearAutomatically}
     * limpia el contexto de persistencia para no dejar entidades desactualizadas en memoria.
     * Nota: al ser un UPDATE masivo, no dispara la auditoria de fecha/usuario de modificacion.
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            UPDATE Cuota c SET c.estadoCuota = com.clubdeportivo.model.EstadoCuota.VENCIDA
            WHERE c.estadoCuota = com.clubdeportivo.model.EstadoCuota.PENDIENTE
              AND c.fechaVencimiento < :hoy
            """)
    int marcarVencidas(@Param("hoy") LocalDate hoy);
}
