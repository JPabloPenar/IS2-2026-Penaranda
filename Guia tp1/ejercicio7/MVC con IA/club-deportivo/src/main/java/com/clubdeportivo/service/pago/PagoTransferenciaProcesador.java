package com.clubdeportivo.service.pago;

import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.model.MedioPago;
import com.clubdeportivo.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Transferencia: exige el número de operación bancaria y que no se haya cargado antes. */
@Component
@RequiredArgsConstructor
public class PagoTransferenciaProcesador implements ProcesadorPago {

    private final PagoRepository pagoRepository;

    @Override
    public MedioPago getMedio() {
        return MedioPago.TRANSFERENCIA;
    }

    @Override
    public void validar(PagoCuotaDTO dto) {
        String referencia = dto.getComprobanteReferencia();
        if (referencia == null || referencia.isBlank()) {
            throw new ReglaNegocioException("comprobanteReferencia",
                    "Ingrese el número de operación de la transferencia");
        }
        if (pagoRepository.existsByMedioPagoAndComprobanteReferencia(MedioPago.TRANSFERENCIA, referencia.trim())) {
            throw new ReglaNegocioException("comprobanteReferencia",
                    "Ese número de operación ya fue registrado en otro pago");
        }
    }
}
