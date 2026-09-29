package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.AccesoResponseDTO;
import com.clubdeportivo.model.Persona;
import com.clubdeportivo.model.RegistroAcceso;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;

/** Convierte un {@link RegistroAcceso} en {@link AccesoResponseDTO}. */
@Component
public class AccesoMapper {

    public static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public AccesoResponseDTO toResponse(RegistroAcceso registro) {
        Persona persona = registro.getPersona();
        return AccesoResponseDTO.builder()
                .id(registro.getId())
                .nombreCompleto(persona.getApellido() + ", " + persona.getNombre())
                .dni(persona.getDni())
                .foto(persona.getFotoRostroUrl())
                .tipoAcceso(registro.getTipoAcceso().getEtiqueta())
                .fechaHora(registro.getFechaHora().format(FORMATO_FECHA_HORA))
                .permitido(registro.isPermitido())
                .observaciones(registro.getObservaciones())
                .build();
    }
}
