package com.colegio.sistemaescolar.service.impl;

import com.colegio.sistemaescolar.dto.NotaDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.exception.RecursoNoEncontradoException;
import com.colegio.sistemaescolar.mapper.NotaMapper;
import com.colegio.sistemaescolar.model.Alumno;
import com.colegio.sistemaescolar.model.Docente;
import com.colegio.sistemaescolar.model.Materia;
import com.colegio.sistemaescolar.model.Nota;
import com.colegio.sistemaescolar.model.Periodo;
import com.colegio.sistemaescolar.repository.AlumnoRepository;
import com.colegio.sistemaescolar.repository.DocenteRepository;
import com.colegio.sistemaescolar.repository.MateriaRepository;
import com.colegio.sistemaescolar.repository.NotaRepository;
import com.colegio.sistemaescolar.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de las calificaciones: registro, edición, consulta con filtros y estadísticas.
 */
@Service
@RequiredArgsConstructor
public class NotaServiceImpl implements NotaService {

    private final NotaRepository notaRepository;
    private final AlumnoRepository alumnoRepository;
    private final MateriaRepository materiaRepository;
    private final DocenteRepository docenteRepository;
    private final NotaMapper notaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<NotaDTO> filtrar(Long alumnoId, Long materiaId, Periodo periodo) {
        return notaRepository.filtrar(alumnoId, materiaId, periodo).stream()
                .map(notaMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public NotaDTO buscarPorId(Long id) {
        Nota nota = notaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Nota", id));
        return notaMapper.toDto(nota);
    }

    @Override
    @Transactional
    public NotaDTO guardar(NotaDTO dto, String emailDocente) {
        boolean esNueva = dto.getId() == null;

        Alumno alumno = alumnoRepository.findById(dto.getAlumnoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getAlumnoId()));
        Materia materia = materiaRepository.findById(dto.getMateriaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Materia", dto.getMateriaId()));

        // Regla: una sola nota por alumno + materia + período.
        // Si existe otra distinta de la que se está editando, se rechaza.
        notaRepository.findByAlumnoIdAndMateriaIdAndPeriodo(alumno.getId(), materia.getId(), dto.getPeriodo())
                .filter(existente -> esNueva || !existente.getId().equals(dto.getId()))
                .ifPresent(existente -> {
                    throw new ReglaNegocioException("periodo",
                            "Este alumno ya tiene una nota de esta materia en ese período");
                });

        Nota nota;
        if (esNueva) {
            nota = new Nota();
            // El autor de la nota es el docente autenticado (solo al crearla)
            Docente docente = docenteRepository.findByEmail(emailDocente).orElse(null);
            nota.setDocente(docente);
        } else {
            nota = notaRepository.findById(dto.getId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Nota", dto.getId()));
        }

        nota.setAlumno(alumno);
        nota.setMateria(materia);
        nota.setPeriodo(dto.getPeriodo());
        nota.setValorNumerico(dto.getValorNumerico());
        nota.setComentarios(dto.getComentarios() == null || dto.getComentarios().isBlank()
                ? null : dto.getComentarios().trim());

        return notaMapper.toDto(notaRepository.save(nota));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!notaRepository.existsById(id)) {
            throw new RecursoNoEncontradoException("Nota", id);
        }
        notaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return notaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public double promedioGeneral() {
        Double promedio = notaRepository.promedioGeneral();
        return promedio == null ? 0.0 : promedio;
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotaDTO> ultimasNotas() {
        return notaRepository.findTop5ByOrderByFechaCreacionDesc().stream()
                .map(notaMapper::toDto).toList();
    }
}
