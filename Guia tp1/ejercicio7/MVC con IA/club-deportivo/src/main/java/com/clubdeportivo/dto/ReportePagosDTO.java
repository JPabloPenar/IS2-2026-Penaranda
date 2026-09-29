package com.clubdeportivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** DTO de SALIDA del reporte de pagos por rango de fechas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportePagosDTO {
    private LocalDate desde;
    private LocalDate hasta;
    private List<PagoResponseDTO> pagos;
    private List<TotalMedioDTO> totales;
    private BigDecimal totalGeneral;
    private long cantidadTotal;
    /** true si el detalle se recortó al límite de filas (los totales SIEMPRE son del rango completo). */
    private boolean detalleLimitado;
}
