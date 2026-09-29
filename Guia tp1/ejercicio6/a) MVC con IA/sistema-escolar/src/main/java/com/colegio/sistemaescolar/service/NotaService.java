package com.colegio.sistemaescolar.service;

import com.colegio.sistemaescolar.dto.NotaDTO;
import com.colegio.sistemaescolar.model.Periodo;

import java.util.List;

/** Contrato de la lógica de negocio de las calificaciones. */
public interface NotaService {

    /** Lista notas con filtros opcionales (cualquier parámetro puede ser null). */
    List<NotaDTO> filtrar(Long alumnoId, Long materiaId, Periodo periodo);

    NotaDTO buscarPorId(Long id);

    /**
     * Crea o actualiza una nota. Al crear, se registra como autor al docente identificado por
     * {@code emailDocente}.
     */
    NotaDTO guardar(NotaDTO dto, String emailDocente);

    void eliminar(Long id);

    long contar();

    /** Promedio general de todas las notas (0.0 si no hay ninguna). */
    double promedioGeneral();

    /** Las notas más recientes, para el panel principal. */
    List<NotaDTO> ultimasNotas();
}
