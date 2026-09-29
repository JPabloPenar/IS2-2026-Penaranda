package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.AccesoRegistroDTO;
import com.clubdeportivo.dto.AccesoResponseDTO;
import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.ResultadoAccesoDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.mapper.AccesoMapper;
import com.clubdeportivo.model.GrupoFamiliar;
import com.clubdeportivo.model.Persona;
import com.clubdeportivo.model.RegistroAcceso;
import com.clubdeportivo.model.TipoAcceso;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.PersonaRepository;
import com.clubdeportivo.repository.RegistroAccesoRepository;
import com.clubdeportivo.service.AccesoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio del control de acceso. La regla central es simple pero crítica:
 * <b>una persona solo entra si el grupo familiar al que pertenece no tiene cuotas adeudadas</b>
 * (vencidas o pendientes con fecha de vencimiento ya pasada). La salida siempre se permite.
 */
@Service
@RequiredArgsConstructor
public class AccesoServiceImpl implements AccesoService {

    private final PersonaRepository personaRepository;
    private final CuotaRepository cuotaRepository;
    private final RegistroAccesoRepository registroAccesoRepository;
    private final AccesoMapper accesoMapper;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public PersonaAccesoDTO buscarParaAcceso(String dni) {
        Persona persona = personaRepository.findByDni(dni.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una persona con DNI " + dni));

        boolean alDia = estaAlDia(persona);
        Optional<RegistroAcceso> ultimo = registroAccesoRepository
                .findFirstByPersonaIdAndPermitidoTrueOrderByFechaHoraDesc(persona.getId());

        String textoUltimo = ultimo.map(r -> r.getTipoAcceso().getEtiqueta() + " "
                        + r.getFechaHora().format(AccesoMapper.FORMATO_FECHA_HORA))
                .orElse("Sin registros previos");

        // Si el ultimo pasaje permitido fue una ENTRADA, lo logico ahora es una SALIDA (y viceversa).
        TipoAcceso sugerido = ultimo.map(RegistroAcceso::getTipoAcceso)
                .map(t -> t == TipoAcceso.ENTRADA ? TipoAcceso.SALIDA : TipoAcceso.ENTRADA)
                .orElse(TipoAcceso.ENTRADA);

        return PersonaAccesoDTO.builder()
                .id(persona.getId())
                .nombreCompleto(persona.getApellido() + ", " + persona.getNombre())
                .dni(persona.getDni())
                .foto(persona.getFotoRostroUrl())
                .tipo(persona.getTipoPersona())
                .relacion(persona.getRelacion())
                .activo(persona.isActivo())
                .alDia(alDia)
                .ultimoAcceso(textoUltimo)
                .tipoSugerido(sugerido)
                .build();
    }

    @Override
    @Transactional
    public ResultadoAccesoDTO registrar(AccesoRegistroDTO dto) {
        Persona persona = personaRepository.findByDni(dto.getDni().trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe una persona con DNI " + dto.getDni()));

        boolean permitido;
        String observaciones;

        if (!persona.isActivo()) {
            permitido = false;
            observaciones = "Rechazado - La persona está dada de baja";
        } else if (dto.getTipoAcceso() == TipoAcceso.SALIDA) {
            // La salida SIEMPRE se permite: nunca se le impide irse a alguien por deuda.
            permitido = true;
            observaciones = "Salida registrada";
        } else if (estaAlDia(persona)) {
            permitido = true;
            observaciones = "Acceso permitido - Cuota al día";
        } else {
            permitido = false;
            observaciones = "Rechazado - Cuota adeudada";
        }

        RegistroAcceso registro = new RegistroAcceso();
        registro.setPersona(persona);
        registro.setTipoAcceso(dto.getTipoAcceso());
        registro.setFechaHora(LocalDateTime.now(clock));
        registro.setPermitido(permitido);
        registro.setObservaciones(observaciones);
        registro = registroAccesoRepository.save(registro);

        return ResultadoAccesoDTO.builder()
                .permitido(permitido)
                .mensaje(observaciones)
                .nombreCompleto(persona.getApellido() + ", " + persona.getNombre())
                .dni(persona.getDni())
                .foto(persona.getFotoRostroUrl())
                .tipoAcceso(dto.getTipoAcceso().getEtiqueta())
                .fechaHora(registro.getFechaHora().format(AccesoMapper.FORMATO_FECHA_HORA))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoResponseDTO> ultimosAccesos() {
        return registroAccesoRepository.findTop10ByOrderByFechaHoraDesc().stream()
                .map(accesoMapper::toResponse)
                .toList();
    }

    /** Una persona sin grupo familiar (dato incompleto) NO puede ingresar: no hay cuota que verificar. */
    private boolean estaAlDia(Persona persona) {
        GrupoFamiliar grupo = persona.getGrupoFamiliar();
        if (grupo == null) {
            return false;
        }
        return cuotaRepository.contarAdeudadas(grupo.getId(), LocalDate.now(clock)) == 0;
    }
}
