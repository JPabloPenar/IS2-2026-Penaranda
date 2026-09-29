package com.clubdeportivo.dto;

import com.clubdeportivo.model.MedioPago;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Subtotal recaudado por medio de pago (parte del reporte). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TotalMedioDTO {
    private MedioPago medio;
    private BigDecimal total;
    private long cantidad;
}
