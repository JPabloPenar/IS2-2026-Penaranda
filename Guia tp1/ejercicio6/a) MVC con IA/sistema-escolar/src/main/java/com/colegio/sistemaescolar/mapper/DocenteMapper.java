package com.colegio.sistemaescolar.mapper;

import com.colegio.sistemaescolar.dto.DocenteRegistroDTO;
import com.colegio.sistemaescolar.dto.DocenteResponseDTO;
import com.colegio.sistemaescolar.model.Docente;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/**
 * Convierte entre {@link Docente} (entidad) y sus DTOs.
 *
 * <p>{@code @Component}: Spring crea una única instancia y permite inyectarla en los servicios.
 * Se escribe a mano (sin MapStruct) para que la conversión sea explícita y fácil de seguir.
 */
@Component
public class DocenteMapper {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Crea una entidad nueva a partir del formulario de registro.
     *
     * @param dto               datos validados del formulario
     * @param passwordHasheada  contraseña YA cifrada con BCrypt (el mapeador nunca cifra)
     */
    public Docente toEntity(DocenteRegistroDTO dto, String passwordHasheada) {
        Docente docente = new Docente();
        docente.setNombre(dto.getNombre().trim());
        docente.setApellido(dto.getApellido().trim());
        docente.setSexo(dto.getSexo());
        docente.setFechaNacimiento(dto.getFechaNacimiento());
        docente.setEmail(dto.getEmail().trim().toLowerCase());
        docente.setPassword(passwordHasheada);
        // rol y activo toman sus valores por defecto de la entidad (ROLE_DOCENTE, true)
        return docente;
    }

    /** Convierte la entidad en un DTO seguro para mostrar (sin contraseña). */
    public DocenteResponseDTO toResponse(Docente docente) {
        return DocenteResponseDTO.builder()
                .id(docente.getId())
                .nombre(docente.getNombre())
                .apellido(docente.getApellido())
                .nombreCompleto(docente.getNombre() + " " + docente.getApellido())
                .email(docente.getEmail())
                .sexo(docente.getSexo().getEtiqueta())
                .fechaNacimiento(docente.getFechaNacimiento())
                .rol(docente.getRol())
                .fechaRegistro(docente.getFechaCreacion() != null
                        ? docente.getFechaCreacion().format(FORMATO_FECHA) : "")
                .build();
    }
}
