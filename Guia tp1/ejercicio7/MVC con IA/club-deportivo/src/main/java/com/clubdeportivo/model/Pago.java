package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pago aplicado a una {@link Cuota}. Una cuota puede tener varios pagos parciales.
 * Los indices aceleran el reporte por fechas y la deteccion de comprobantes repetidos.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pagos",
        indexes = {
                @Index(name = "idx_pago_fecha", columnList = "fecha_pago"),
                @Index(name = "idx_pago_medio_comprobante", columnList = "medio_pago, comprobante_referencia")
        })
public class Pago extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cuota_id", nullable = false)
    private Cuota cuota;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    /** Recibo interno (efectivo), numero de operacion (transferencia) o ID de Mercado Pago. */
    @Column(name = "comprobante_referencia", length = 100)
    private String comprobanteReferencia;

    @Enumerated(EnumType.STRING)
    @Column(name = "medio_pago", nullable = false, length = 20)
    private MedioPago medioPago;
}
