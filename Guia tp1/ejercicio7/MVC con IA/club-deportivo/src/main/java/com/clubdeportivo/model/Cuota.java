package com.clubdeportivo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cuota mensual de un {@link GrupoFamiliar}. Puede saldarse con uno o varios {@link Pago}.
 *
 * <p>Restriccion unica: no puede haber dos cuotas del mismo grupo para el mismo mes y anio.
 * El indice (estado, vencimiento) acelera la consulta "cuotas adeudadas" que se ejecuta en
 * cada ingreso al club.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "cuotas",
        uniqueConstraints = @UniqueConstraint(name = "uk_cuota_grupo_periodo",
                columnNames = {"grupo_familiar_id", "mes_periodo", "anio_periodo"}),
        indexes = @Index(name = "idx_cuota_estado_vencimiento", columnList = "estado_cuota, fecha_vencimiento"))
public class Cuota extends BaseAuditableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "grupo_familiar_id", nullable = false)
    private GrupoFamiliar grupoFamiliar;

    /** Mes del periodo (1 a 12). */
    @Column(name = "mes_periodo", nullable = false)
    private int mesPeriodo;

    @Column(name = "anio_periodo", nullable = false)
    private int anioPeriodo;

    /** BigDecimal (no double) para dinero: evita errores de redondeo. */
    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cuota", nullable = false, length = 15)
    private EstadoCuota estadoCuota = EstadoCuota.PENDIENTE;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    /** Pagos aplicados a esta cuota (lado inverso de Pago.cuota). */
    @OneToMany(mappedBy = "cuota", fetch = FetchType.LAZY)
    private List<Pago> pagos = new ArrayList<>();
}
