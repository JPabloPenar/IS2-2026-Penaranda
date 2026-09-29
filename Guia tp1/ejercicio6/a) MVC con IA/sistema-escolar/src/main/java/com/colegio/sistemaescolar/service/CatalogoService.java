package com.colegio.sistemaescolar.service;

import com.colegio.sistemaescolar.dto.AulaDTO;
import com.colegio.sistemaescolar.dto.GradoDTO;
import com.colegio.sistemaescolar.dto.MateriaDTO;

import java.util.List;

/** Entrega los catálogos (grados, aulas, materias) que alimentan las listas desplegables. */
public interface CatalogoService {

    List<GradoDTO> listarGrados();

    List<AulaDTO> listarAulas();

    List<MateriaDTO> listarMaterias();

    long contarMaterias();
}
