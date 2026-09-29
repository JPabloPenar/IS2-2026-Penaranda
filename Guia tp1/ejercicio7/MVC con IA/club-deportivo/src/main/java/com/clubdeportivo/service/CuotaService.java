package com.clubdeportivo.service;

import com.clubdeportivo.dto.CuotaDTO;
import com.clubdeportivo.dto.CuotaResponseDTO;
import com.clubdeportivo.model.EstadoCuota;

import java.util.List;

/** Contrato de la lógica de negocio de emisión y consulta de cuotas. */
public interface CuotaService {

    /** Emite una cuota nueva para el grupo familiar del socio indicado. */
    CuotaResponseDTO emitir(CuotaDTO dto);

    /** Lista cuotas, opcionalmente filtradas por estado (null = todas). */
    List<CuotaResponseDTO> listar(EstadoCuota estado);

    CuotaResponseDTO buscarPorId(Long id);
}
