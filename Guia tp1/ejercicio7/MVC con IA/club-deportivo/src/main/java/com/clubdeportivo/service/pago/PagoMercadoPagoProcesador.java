package com.clubdeportivo.service.pago;

import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.model.MedioPago;
import com.clubdeportivo.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Mercado Pago: exige el ID de pago que devuelve la plataforma y que no se haya usado antes.
 *
 * <p>Aquí es donde, en una integración real, se llamaría a la API de Mercado Pago
 * (GET /v1/payments/{id}) para confirmar que el pago existe, está aprobado y su monto coincide.
 * Se deja como comentario para no depender de una llamada externa real en este proyecto.
 */
@Component
@RequiredArgsConstructor
public class PagoMercadoPagoProcesador implements ProcesadorPago {

    private final PagoRepository pagoRepository;

    @Override
    public MedioPago getMedio() {
        return MedioPago.MERCADO_PAGO;
    }

    @Override
    public void validar(PagoCuotaDTO dto) {
        String referencia = dto.getComprobanteReferencia();
        if (referencia == null || referencia.isBlank()) {
            throw new ReglaNegocioException("comprobanteReferencia", "Ingrese el ID de pago de Mercado Pago");
        }
        if (pagoRepository.existsByMedioPagoAndComprobanteReferencia(MedioPago.MERCADO_PAGO, referencia.trim())) {
            throw new ReglaNegocioException("comprobanteReferencia", "Ese ID de pago ya fue registrado");
        }
        // TODO (integración real): verificar contra la API de Mercado Pago el estado "approved"
        // y que dto.getMontoPagado() coincida con el monto informado por la plataforma.
    }
}
