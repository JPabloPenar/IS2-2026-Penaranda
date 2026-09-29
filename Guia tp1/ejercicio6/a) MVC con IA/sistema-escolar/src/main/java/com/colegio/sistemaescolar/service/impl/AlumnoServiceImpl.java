package com.colegio.sistemaescolar.service.impl;

import com.colegio.sistemaescolar.dto.AlumnoDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.exception.RecursoNoEncontradoException;
import com.colegio.sistemaescolar.mapper.AlumnoMapper;
import com.colegio.sistemaescolar.model.Alumno;
import com.colegio.sistemaescolar.model.Aula;
import com.colegio.sistemaescolar.model.Grado;
import com.colegio.sistemaescolar.repository.AlumnoRepository;
import com.colegio.sistemaescolar.repository.AulaRepository;
import com.colegio.sistemaescolar.repository.GradoRepository;
import com.colegio.sistemaescolar.service.AlumnoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de los alumnos: CRUD y reglas
 * (DNI único, capacidad máxima del aula).
 */
@Service
@RequiredArgsConstructor
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;
    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final AlumnoMapper alumnoMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AlumnoDTO> listar(String busqueda) {
        String texto = busqueda == null ? "" : busqueda.trim();
        return alumnoRepository.buscar(texto).stream().map(alumnoMapper::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AlumnoDTO buscarPorId(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", id));
        return alumnoMapper.toDto(alumno);
    }

    @Override
    @Transactional
    public AlumnoDTO guardar(AlumnoDTO dto) {
        boolean esNuevo = dto.getId() == null;
        String dni = dto.getDni().trim();

        // Regla 1: el DNI/matrícula no puede repetirse (al editar se excluye al propio alumno)
        boolean dniDuplicado = esNuevo
                ? alumnoRepository.existsByDni(dni)
                : alumnoRepository.existsByDniAndIdNot(dni, dto.getId());
        if (dniDuplicado) {
            throw new ReglaNegocioException("dni", "Ya existe un alumno con este DNI o matrícula");
        }

        // Se resuelven las relaciones a partir de los ids que llegan del formulario
        Grado grado = gradoRepository.findById(dto.getGradoId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Grado", dto.getGradoId()));
        Aula aula = aulaRepository.findById(dto.getAulaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Aula", dto.getAulaId()));

        Alumno alumno = esNuevo
                ? new Alumno()
                : alumnoRepository.findById(dto.getId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", dto.getId()));

        // Regla 2: el aula no puede superar su capacidad.
        // Si el alumno ya estaba en esa aula, no cuenta como un lugar adicional.
        boolean mismaAula = !esNuevo
                && alumno.getAula() != null
                && alumno.getAula().getId().equals(aula.getId());
        if (!mismaAula && alumnoRepository.countByAulaId(aula.getId()) >= aula.getCapacidad()) {
            throw new ReglaNegocioException("aulaId",
                    aula.getCodigo() + " ya alcanzó su capacidad de " + aula.getCapacidad() + " alumnos");
        }

        alumnoMapper.actualizarEntidad(dto, alumno, grado, aula);
        return alumnoMapper.toDto(alumnoRepository.save(alumno));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Alumno", id));
        // CascadeType.REMOVE en Alumno.notas elimina también sus calificaciones
        alumnoRepository.delete(alumno);
    }

    @Override
    @Transactional(readOnly = true)
    public long contar() {
        return alumnoRepository.count();
    }
}
