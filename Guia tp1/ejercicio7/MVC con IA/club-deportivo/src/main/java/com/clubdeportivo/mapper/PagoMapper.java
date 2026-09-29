package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.PagoResponseDTO;
import com.clubdeportivo.model.Cuota;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.model.Socio;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/** Convierte un {@link Pago} en {@link PagoResponseDTO}. */
@Component
public class PagoMapper {

    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public PagoResponseDTO toResponse(Pago pago) {
        Cuota cuota = pago.getCuota();
        Socio titular = cuota.getGrupoFamiliar().getTitular();
        return PagoResponseDTO.builder()
                .id(pago.getId())
                .cuotaId(cuota.getId())
                .periodo(String.format("%02d/%d", cuota.getMesPeriodo(), cuota.getAnioPeriodo()))
                .titularNombre(titular.getApellido() + ", " + titular.getNombre())
                .titularDni(titular.getDni())
                .montoPagado(pago.getMontoPagado())
                .fechaPago(pago.getFechaPago().format(FORMATO))
                .medioPago(pago.getMedioPago())
                .comprobanteReferencia(pago.getComprobanteReferencia())
                .build();
    }
}
