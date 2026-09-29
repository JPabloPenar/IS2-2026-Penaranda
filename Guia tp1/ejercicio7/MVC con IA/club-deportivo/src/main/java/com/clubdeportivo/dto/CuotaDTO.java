package com.clubdeportivo.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

/** DTO de ENTRADA para emitir una cuota a un grupo familiar (se identifica por su socio titular). */
@Getter
@Setter
@NoArgsConstructor
public class CuotaDTO {

    @NotNull(message = "Seleccione el socio")
    private Long socioId;

    @NotNull(message = "Indique el mes")
    @Min(value = 1, message = "El mes debe estar entre 1 y 12")
    @Max(value = 12, message = "El mes debe estar entre 1 y 12")
    private Integer mesPeriodo;

    @NotNull(message = "Indique el año")
    @Min(value = 2020, message = "El año debe ser 2020 o posterior")
    @Max(value = 2100, message = "El año no es válido")
    private Integer anioPeriodo;

    @NotNull(message = "Indique el monto")
    @Positive(message = "El monto debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "Use hasta 2 decimales")
    private BigDecimal montoTotal;

    @NotNull(message = "Indique la fecha de vencimiento")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaVencimiento;
}
