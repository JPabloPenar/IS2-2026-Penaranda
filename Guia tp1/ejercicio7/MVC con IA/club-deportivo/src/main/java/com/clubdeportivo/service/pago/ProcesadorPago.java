package com.clubdeportivo.service.pago;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.model.MedioPago;

/**
 * Patrón Strategy: cada medio de pago valida sus propios requisitos antes de aplicar el cobro
 * (por ejemplo, Mercado Pago exige un ID de operación y efectivo no exige nada).
 * {@link PagoServiceImpl} elige la implementación correcta según {@link PagoCuotaDTO#getMedioPago()}.
 */
public interface ProcesadorPago {

    /** Medio de pago que esta estrategia sabe procesar. */
    MedioPago getMedio();

    /**
     * Valida las reglas propias del medio de pago (p. ej. formato o unicidad del comprobante).
     *
     * @throws com.clubdeportivo.exception.ReglaNegocioException si el DTO no cumple las reglas de este medio
     */
    void validar(PagoCuotaDTO dto);
}
