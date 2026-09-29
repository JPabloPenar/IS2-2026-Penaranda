package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.CuotaDTO;
import com.clubdeportivo.dto.CuotaResponseDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.mapper.CuotaMapper;
import com.clubdeportivo.model.Cuota;
import com.clubdeportivo.model.EstadoCuota;
import com.clubdeportivo.model.GrupoFamiliar;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.GrupoFamiliarRepository;
import com.clubdeportivo.service.CuotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de emisión y consulta de cuotas. */
@Service
@RequiredArgsConstructor
public class CuotaServiceImpl implements CuotaService {

    private final CuotaRepository cuotaRepository;
    private final GrupoFamiliarRepository grupoFamiliarRepository;
    private final CuotaMapper cuotaMapper;

    @Override
    @Transactional
    public CuotaResponseDTO emitir(CuotaDTO dto) {
        GrupoFamiliar grupo = grupoFamiliarRepository.findByTitularId(dto.getSocioId())
                .orElseThrow(() -> new ReglaNegocioException("socioId", "El socio todavía no tiene un grupo familiar"));

        // Regla: no se puede emitir dos veces la cuota del mismo grupo para el mismo mes y año.
        if (cuotaRepository.existsByGrupoFamiliarIdAndMesPeriodoAndAnioPeriodo(
                grupo.getId(), dto.getMesPeriodo(), dto.getAnioPeriodo())) {
            throw new ReglaNegocioException("anioPeriodo",
                    "Ya existe una cuota de " + dto.getMesPeriodo() + "/" + dto.getAnioPeriodo() + " para este socio");
        }

        Cuota cuota = new Cuota();
        cuota.setGrupoFamiliar(grupo);
        cuota.setMesPeriodo(dto.getMesPeriodo());
        cuota.setAnioPeriodo(dto.getAnioPeriodo());
        cuota.setMontoTotal(dto.getMontoTotal());
        cuota.setFechaVencimiento(dto.getFechaVencimiento());
        cuota.setEstadoCuota(EstadoCuota.PENDIENTE);
        cuota = cuotaRepository.save(cuota);

        // Se relee con JOIN FETCH para que CuotaMapper pueda acceder a grupo/titular/pagos sin lazy-loading.
        return cuotaMapper.toResponse(cuotaRepository.buscarConDetalle(cuota.getId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuota", cuota.getId())));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuotaResponseDTO> listar(EstadoCuota estado) {
        return cuotaRepository.listarConPagos(estado).stream().map(cuotaMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CuotaResponseDTO buscarPorId(Long id) {
        Cuota cuota = cuotaRepository.buscarConDetalle(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuota", id));
        return cuotaMapper.toResponse(cuota);
    }
}
