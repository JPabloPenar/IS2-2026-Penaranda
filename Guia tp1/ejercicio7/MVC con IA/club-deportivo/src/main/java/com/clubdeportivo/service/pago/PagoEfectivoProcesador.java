package com.clubdeportivo.service.pago;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.model.MedioPago;
import org.springframework.stereotype.Component;

/** Efectivo: no exige comprobante (se cobra en el mostrador y no hay operación que verificar). */
@Component
public class PagoEfectivoProcesador implements ProcesadorPago {

    @Override
    public MedioPago getMedio() {
        return MedioPago.EFECTIVO;
    }

    @Override
    public void validar(PagoCuotaDTO dto) {
        // Sin reglas adicionales: el monto y la cuota ya los valida PagoServiceImpl para todos los medios.
    }
}
