package com.clubdeportivo.dto;

import com.clubdeportivo.model.EstadoCuota;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO de SALIDA de una cuota, con lo pagado y el saldo ya calculados. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuotaResponseDTO {
    private Long id;
    /** Periodo formateado "MM/AAAA". */
    private String periodo;
    private Long socioId;
    private String titularNombre;
    private String titularDni;
    private BigDecimal montoTotal;
    private BigDecimal montoPagado;
    private BigDecimal saldo;
    private EstadoCuota estado;
    private LocalDate fechaVencimiento;
}
