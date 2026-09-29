package com.colegio.sistemaescolar.mapper;

import com.colegio.sistemaescolar.dto.AulaDTO;
import com.colegio.sistemaescolar.dto.GradoDTO;
import com.colegio.sistemaescolar.dto.MateriaDTO;
import com.colegio.sistemaescolar.model.Aula;
import com.colegio.sistemaescolar.model.Grado;
import com.colegio.sistemaescolar.model.Materia;
import org.springframework.stereotype.Component;

/** Convierte las entidades de catálogo (Grado, Aula, Materia) en DTOs para las listas desplegables. */
@Component
public class CatalogoMapper {

    public GradoDTO toDto(Grado grado) {
        return new GradoDTO(grado.getId(), grado.getNombre(), grado.getNivel().getEtiqueta());
    }

    public AulaDTO toDto(Aula aula) {
        return new AulaDTO(aula.getId(), aula.getCodigo(), aula.getCapacidad());
    }

    public MateriaDTO toDto(Materia materia) {
        return new MateriaDTO(materia.getId(), materia.getNombre(), materia.getDescripcion());
    }
}
