package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.FamiliarRegistroDTO;
import com.clubdeportivo.dto.PersonaRegistroDTO;
import com.clubdeportivo.dto.PersonaResponseDTO;
import com.clubdeportivo.dto.SocioRegistroDTO;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.GrupoFamiliar;
import com.clubdeportivo.model.Persona;
import com.clubdeportivo.model.Socio;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Convierte entre las entidades de persona ({@link Socio}, {@link Familiar}) y sus DTOs.
 *
 * <p>{@code @Component}: Spring crea una única instancia inyectable en los servicios.
 * El mapeo es manual (sin MapStruct) para que cada campo copiado sea explícito y fácil de auditar.
 */
@Component
public class PersonaMapper {

    /** DTO de alta -> entidad Socio nueva. {@code nombreFoto} ya fue guardado por FileStorageService. */
    public Socio toSocio(SocioRegistroDTO dto, String nombreFoto, LocalDate fechaAlta) {
        Socio socio = new Socio();
        copiarDatos(dto, socio, nombreFoto);
        socio.setFechaAlta(fechaAlta);
        return socio;
    }

    /** DTO de alta -> entidad Familiar nueva, ya vinculada al grupo del titular. */
    public Familiar toFamiliar(FamiliarRegistroDTO dto, String nombreFoto, GrupoFamiliar grupo) {
        Familiar familiar = new Familiar();
        copiarDatos(dto, familiar, nombreFoto);
        familiar.setParentesco(dto.getParentesco());
        familiar.setGrupoFamiliar(grupo);
        return familiar;
    }

    /** Entidad -> DTO de salida. {@code alDia} puede ser null si no se calcula. */
    public PersonaResponseDTO toResponse(Persona p, Boolean alDia) {
        return PersonaResponseDTO.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .apellido(p.getApellido())
                .nombreCompleto(p.getApellido() + ", " + p.getNombre())
                .dni(p.getDni())
                .email(p.getEmail())
                .telefono(p.getTelefono())
                .fechaNacimiento(p.getFechaNacimiento())
                .foto(p.getFotoRostroUrl())
                .tipo(p.getTipoPersona())
                .relacion(p.getRelacion())
                .activo(p.isActivo())
                .alDia(alDia)
                .build();
    }

    private void copiarDatos(PersonaRegistroDTO dto, Persona p, String nombreFoto) {
        p.setNombre(dto.getNombre().trim());
        p.setApellido(dto.getApellido().trim());
        p.setDni(dto.getDni().trim());
        p.setFechaNacimiento(dto.getFechaNacimiento());
        p.setEmail(vacioANulo(dto.getEmail()));
        p.setTelefono(vacioANulo(dto.getTelefono()));
        p.setFotoRostroUrl(nombreFoto);
        p.setActivo(true);
    }

    private String vacioANulo(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
