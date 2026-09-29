package com.colegio.sistemaescolar.mapper;

import com.colegio.sistemaescolar.dto.NotaDTO;
import com.colegio.sistemaescolar.model.Nota;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

/** Convierte una {@link Nota} en {@link NotaDTO}. Se usa dentro de transacciones del servicio. */
@Component
public class NotaMapper {

    /** Nota mínima para considerar aprobada una materia (ajustable según el reglamento). */
    private static final BigDecimal NOTA_MINIMA_APROBACION = new BigDecimal("6.00");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public NotaDTO toDto(Nota nota) {
        NotaDTO dto = new NotaDTO();
        dto.setId(nota.getId());
        dto.setAlumnoId(nota.getAlumno().getId());
        dto.setAlumnoNombre(nota.getAlumno().getApellido() + ", " + nota.getAlumno().getNombre());
        dto.setMateriaId(nota.getMateria().getId());
        dto.setMateriaNombre(nota.getMateria().getNombre());
        dto.setPeriodo(nota.getPeriodo());
        dto.setValorNumerico(nota.getValorNumerico());
        dto.setComentarios(nota.getComentarios());
        dto.setAprobado(nota.getValorNumerico().compareTo(NOTA_MINIMA_APROBACION) >= 0);

        if (nota.getDocente() != null) {
            dto.setDocenteNombre(nota.getDocente().getNombre() + " " + nota.getDocente().getApellido());
        }
        if (nota.getFechaCreacion() != null) {
            dto.setFechaRegistro(nota.getFechaCreacion().format(FORMATO_FECHA_HORA));
        }
        return dto;
    }
}
