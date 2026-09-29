package com.colegio.sistemaescolar.mapper;

import com.colegio.sistemaescolar.dto.AlumnoDTO;
import com.colegio.sistemaescolar.model.Alumno;
import com.colegio.sistemaescolar.model.Aula;
import com.colegio.sistemaescolar.model.Grado;
import org.springframework.stereotype.Component;

/**
 * Convierte entre {@link Alumno} y {@link AlumnoDTO}.
 * Debe invocarse dentro de una transacción (capa de servicio) porque accede a
 * relaciones LAZY (grado y aula).
 */
@Component
public class AlumnoMapper {

    /** Entidad -> DTO (para tablas y para precargar el formulario de edición). */
    public AlumnoDTO toDto(Alumno alumno) {
        AlumnoDTO dto = new AlumnoDTO();
        dto.setId(alumno.getId());
        dto.setNombre(alumno.getNombre());
        dto.setApellido(alumno.getApellido());
        dto.setNombreCompleto(alumno.getApellido() + ", " + alumno.getNombre());
        dto.setDni(alumno.getDni());
        dto.setFechaNacimiento(alumno.getFechaNacimiento());
        dto.setGenero(alumno.getGenero());

        if (alumno.getGrado() != null) {
            dto.setGradoId(alumno.getGrado().getId());
            dto.setGradoNombre(alumno.getGrado().getNombre());
        }
        if (alumno.getAula() != null) {
            dto.setAulaId(alumno.getAula().getId());
            dto.setAulaCodigo(alumno.getAula().getCodigo());
        }
        return dto;
    }

    /**
     * Copia los datos del formulario sobre una entidad (nueva o existente).
     * Grado y aula ya vienen resueltos como entidades desde el servicio.
     */
    public void actualizarEntidad(AlumnoDTO dto, Alumno alumno, Grado grado, Aula aula) {
        alumno.setNombre(dto.getNombre().trim());
        alumno.setApellido(dto.getApellido().trim());
        alumno.setDni(dto.getDni().trim());
        alumno.setFechaNacimiento(dto.getFechaNacimiento());
        alumno.setGenero(dto.getGenero());
        alumno.setGrado(grado);
        alumno.setAula(aula);
    }
}
