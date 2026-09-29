package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.CuotaResponseDTO;
import com.clubdeportivo.model.Cuota;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.model.Socio;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Convierte una {@link Cuota} en {@link CuotaResponseDTO} calculando lo pagado y el saldo.
 * Requiere que la cuota llegue con grupo, titular y pagos cargados (las consultas del repositorio
 * usan JOIN FETCH para hacerlo en una sola consulta y evitar el problema N+1).
 */
@Component
public class CuotaMapper {

    public CuotaResponseDTO toResponse(Cuota cuota) {
        BigDecimal pagado = cuota.getPagos().stream()
                .map(Pago::getMontoPagado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Socio titular = cuota.getGrupoFamiliar().getTitular();

        return CuotaResponseDTO.builder()
                .id(cuota.getId())
                .periodo(String.format("%02d/%d", cuota.getMesPeriodo(), cuota.getAnioPeriodo()))
                .socioId(titular.getId())
                .titularNombre(titular.getApellido() + ", " + titular.getNombre())
                .titularDni(titular.getDni())
                .montoTotal(cuota.getMontoTotal())
                .montoPagado(pagado)
                .saldo(cuota.getMontoTotal().subtract(pagado).max(BigDecimal.ZERO))
                .estado(cuota.getEstadoCuota())
                .fechaVencimiento(cuota.getFechaVencimiento())
                .build();
    }
}
