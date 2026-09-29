package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.PersonaResponseDTO;
import com.clubdeportivo.mapper.PersonaMapper;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final SocioRepository socioRepository;
    private final PersonaMapper personaMapper;

    @Override
    public List<PersonaResponseDTO> listarSociosActivos() {
        return socioRepository.findAll(Sort.by("apellido", "nombre")).stream()
                .filter(Socio::isActivo)
                .map(s -> personaMapper.toResponse(s, null))
                .toList();
    }
}
