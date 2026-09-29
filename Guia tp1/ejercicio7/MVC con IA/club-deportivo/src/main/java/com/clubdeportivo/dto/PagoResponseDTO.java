package com.clubdeportivo.dto;

import com.clubdeportivo.model.MedioPago;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** DTO de SALIDA de un pago registrado. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private Long cuotaId;
    private String periodo;
    private String titularNombre;
    private String titularDni;
    private BigDecimal montoPagado;
    private String fechaPago;
    private MedioPago medioPago;
    private String comprobanteReferencia;
}
