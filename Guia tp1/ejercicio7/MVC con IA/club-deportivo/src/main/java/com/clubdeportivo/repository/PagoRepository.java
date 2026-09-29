package com.clubdeportivo.repository;

import com.clubdeportivo.model.MedioPago;
import com.clubdeportivo.model.Pago;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Acceso a datos de {@link Pago}. */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /** Suma lo ya pagado de una cuota. Devuelve null si aun no tiene pagos. */
    @Query("SELECT SUM(p.montoPagado) FROM Pago p WHERE p.cuota.id = :cuotaId")
    BigDecimal sumarMontoPorCuota(@Param("cuotaId") Long cuotaId);

    /** Anti-duplicados: ¿ya se uso este comprobante con este medio de pago? */
    boolean existsByMedioPagoAndComprobanteReferencia(MedioPago medioPago, String comprobanteReferencia);

    /**
     * Detalle del reporte: pagos del rango [desde, hasta). {@code Pageable} limita las filas
     * (el reporte nunca trae un rango entero a memoria). Las relaciones N:1 se cargan con JOIN FETCH.
     */
    @Query("""
            SELECT p FROM Pago p
              JOIN FETCH p.cuota c
              JOIN FETCH c.grupoFamiliar g
              JOIN FETCH g.titular
            WHERE p.fechaPago >= :desde AND p.fechaPago < :hasta
            ORDER BY p.fechaPago DESC
            """)
    List<Pago> reporte(@Param("desde") LocalDateTime desde,
                       @Param("hasta") LocalDateTime hasta,
                       Pageable pageable);

    /** Totales por medio de pago calculados en la base de datos (GROUP BY), sobre el rango completo. */
    @Query("""
            SELECT p.medioPago AS medioPago, SUM(p.montoPagado) AS total, COUNT(p) AS cantidad
            FROM Pago p
            WHERE p.fechaPago >= :desde AND p.fechaPago < :hasta
            GROUP BY p.medioPago
            """)
    List<TotalPorMedio> totalesPorMedio(@Param("desde") LocalDateTime desde,
                                        @Param("hasta") LocalDateTime hasta);
}
