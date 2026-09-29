package com.colegio.sistemaescolar.service.impl;

import com.colegio.sistemaescolar.dto.AulaDTO;
import com.colegio.sistemaescolar.dto.GradoDTO;
import com.colegio.sistemaescolar.dto.MateriaDTO;
import com.colegio.sistemaescolar.mapper.CatalogoMapper;
import com.colegio.sistemaescolar.repository.AulaRepository;
import com.colegio.sistemaescolar.repository.GradoRepository;
import com.colegio.sistemaescolar.repository.MateriaRepository;
import com.colegio.sistemaescolar.service.CatalogoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Entrega los catálogos ordenados para las listas desplegables de los formularios. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CatalogoServiceImpl implements CatalogoService {

    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final MateriaRepository materiaRepository;
    private final CatalogoMapper catalogoMapper;

    @Override
    public List<GradoDTO> listarGrados() {
        // Por id: respeta el orden natural de creación (1° a 6° de Primaria, luego Secundaria)
        return gradoRepository.findAll(Sort.by("id")).stream().map(catalogoMapper::toDto).toList();
    }

    @Override
    public List<AulaDTO> listarAulas() {
        return aulaRepository.findAll(Sort.by("codigo")).stream().map(catalogoMapper::toDto).toList();
    }

    @Override
    public List<MateriaDTO> listarMaterias() {
        return materiaRepository.findAll(Sort.by("nombre")).stream().map(catalogoMapper::toDto).toList();
    }

    @Override
    public long contarMaterias() {
        return materiaRepository.count();
    }
}
