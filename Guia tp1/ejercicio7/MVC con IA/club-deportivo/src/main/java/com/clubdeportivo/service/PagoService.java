package com.clubdeportivo.service;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoResponseDTO;
import com.clubdeportivo.dto.ReportePagosDTO;

import java.time.LocalDate;

/** Contrato de la lógica de negocio de los pagos y su impacto en el estado de las cuotas. */
public interface PagoService {

    /** Registra un cobro sobre una cuota, según su medio de pago, y actualiza el estado de la cuota. */
    PagoResponseDTO registrarPago(PagoCuotaDTO dto);

    /** Reporte de pagos entre dos fechas (inclusive), con totales por medio de pago. */
    ReportePagosDTO reporte(LocalDate desde, LocalDate hasta);
}
