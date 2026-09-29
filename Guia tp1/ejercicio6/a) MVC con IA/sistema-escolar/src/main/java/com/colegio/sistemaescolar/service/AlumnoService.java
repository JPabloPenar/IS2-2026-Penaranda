package com.colegio.sistemaescolar.service;

import com.colegio.sistemaescolar.dto.AlumnoDTO;

import java.util.List;

/** Contrato de la lógica de negocio de los alumnos. */
public interface AlumnoService {

    /** Lista alumnos; si {@code busqueda} es null o vacío devuelve todos. */
    List<AlumnoDTO> listar(String busqueda);

    AlumnoDTO buscarPorId(Long id);

    /** Crea (id null) o actualiza (id informado) un alumno aplicando las reglas de negocio. */
    AlumnoDTO guardar(AlumnoDTO dto);

    void eliminar(Long id);

    long contar();
}
