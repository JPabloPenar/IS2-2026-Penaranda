package com.clubdeportivo.dto;

import com.clubdeportivo.model.MedioPago;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** DTO de ENTRADA del formulario de cobro de una cuota. */
@Getter
@Setter
@NoArgsConstructor
public class PagoCuotaDTO {

    @NotNull(message = "Falta indicar la cuota")
    private Long cuotaId;

    @NotNull(message = "Indique el monto a cobrar")
    @Positive(message = "El monto debe ser mayor a cero")
    @Digits(integer = 10, fraction = 2, message = "Use hasta 2 decimales")
    private BigDecimal montoPagado;

    @NotNull(message = "Seleccione el medio de pago")
    private MedioPago medioPago;

    /**
     * Efectivo: opcional. Transferencia: número de operación (obligatorio).
     * Mercado Pago: ID del pago (obligatorio). Cada ProcesadorPago aplica su propia regla.
     */
    @Size(max = 100, message = "La referencia no puede superar los 100 caracteres")
    private String comprobanteReferencia;
}
