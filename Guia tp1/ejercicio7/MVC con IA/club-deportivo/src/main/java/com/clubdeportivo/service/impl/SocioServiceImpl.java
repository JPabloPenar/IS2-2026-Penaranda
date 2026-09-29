package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.FamiliarRegistroDTO;
import com.clubdeportivo.dto.PersonaResponseDTO;
import com.clubdeportivo.dto.SocioDetalleDTO;
import com.clubdeportivo.dto.SocioRegistroDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.mapper.CuotaMapper;
import com.clubdeportivo.mapper.PersonaMapper;
import com.clubdeportivo.model.Familiar;
import com.clubdeportivo.model.GrupoFamiliar;
import com.clubdeportivo.model.Persona;
import com.clubdeportivo.model.Socio;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.FamiliarRepository;
import com.clubdeportivo.repository.GrupoFamiliarRepository;
import com.clubdeportivo.repository.PersonaRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.service.FileStorageService;
import com.clubdeportivo.service.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * Lógica de negocio de socios, familiares y grupos familiares.
 *
 * <p>{@code @Transactional} abre una transacción de base de datos por método: si algo falla,
 * TODOS los cambios se revierten (por ejemplo, si falla el alta del grupo familiar, el socio
 * recién creado también se descarta).
 */
@Service
@RequiredArgsConstructor
public class SocioServiceImpl implements SocioService {

    private final SocioRepository socioRepository;
    private final FamiliarRepository familiarRepository;
    private final GrupoFamiliarRepository grupoFamiliarRepository;
    private final PersonaRepository personaRepository;
    private final CuotaRepository cuotaRepository;
    private final PersonaMapper personaMapper;
    private final CuotaMapper cuotaMapper;
    private final FileStorageService fileStorageService;
    private final Clock clock;

    @Override
    @Transactional(readOnly = true)
    public List<PersonaResponseDTO> listar(String busqueda) {
        String texto = busqueda == null ? "" : busqueda.trim();
        LocalDate hoy = LocalDate.now(clock);
        // Un solo query trae TODOS los grupos con deuda; evita consultar "¿está al día?" socio por socio.
        Set<Long> gruposConDeuda = Set.copyOf(cuotaRepository.gruposConDeuda(hoy));

        return socioRepository.buscar(texto).stream()
                .map(socio -> {
                    Long grupoId = socio.getGrupoFamiliar() != null ? socio.getGrupoFamiliar().getId() : null;
                    boolean alDia = grupoId == null || !gruposConDeuda.contains(grupoId);
                    return personaMapper.toResponse(socio, alDia);
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public SocioDetalleDTO buscarDetalle(Long socioId) {
        Socio socio = socioRepository.findById(socioId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", socioId));
        GrupoFamiliar grupo = socio.getGrupoFamiliar();

        List<Familiar> familiares = grupo != null
                ? familiarRepository.findByGrupoFamiliarIdOrderByApellidoAscNombreAsc(grupo.getId())
                : List.of();

        List<com.clubdeportivo.dto.CuotaResponseDTO> cuotas = grupo != null
                ? cuotaRepository.listarPorGrupo(grupo.getId()).stream().map(cuotaMapper::toResponse).toList()
                : List.of();

        boolean alDia = grupo == null || cuotaRepository.contarAdeudadas(grupo.getId(), LocalDate.now(clock)) == 0;

        return SocioDetalleDTO.builder()
                .socio(personaMapper.toResponse(socio, alDia))
                .grupoFamiliarId(grupo != null ? grupo.getId() : null)
                .familiares(familiares.stream().map(f -> personaMapper.toResponse(f, null)).toList())
                .cuotas(cuotas)
                .alDia(alDia)
                .build();
    }

    @Override
    @Transactional
    public PersonaResponseDTO registrarSocio(SocioRegistroDTO dto) {
        String dni = dto.getDni().trim();
        if (personaRepository.existsByDni(dni)) {
            throw new ReglaNegocioException("dni", "Ya existe una persona registrada con este DNI");
        }

        // 1) Se guarda la foto ANTES de tocar la base de datos: si falla, no queda ningun registro a medias.
        String nombreFoto = fileStorageService.guardar(dto.getFoto());

        // 2) Se crea el socio y, de inmediato, el grupo familiar que encabeza.
        Socio socio = personaMapper.toSocio(dto, nombreFoto, LocalDate.now(clock));
        socio = socioRepository.save(socio);

        GrupoFamiliar grupo = new GrupoFamiliar();
        grupo.setTitular(socio);
        grupo = grupoFamiliarRepository.save(grupo);

        socio.setGrupoFamiliar(grupo);
        socio = socioRepository.save(socio);

        return personaMapper.toResponse(socio, true); // recien creado: sin cuotas, esta al dia
    }

    @Override
    @Transactional
    public PersonaResponseDTO registrarFamiliar(FamiliarRegistroDTO dto) {
        String dni = dto.getDni().trim();
        if (personaRepository.existsByDni(dni)) {
            throw new ReglaNegocioException("dni", "Ya existe una persona registrada con este DNI");
        }

        Socio titular = socioRepository.findById(dto.getSocioId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Socio", dto.getSocioId()));
        GrupoFamiliar grupo = grupoFamiliarRepository.findByTitularId(titular.getId())
                .orElseThrow(() -> new ReglaNegocioException("socioId", "El socio todavía no tiene un grupo familiar"));

        String nombreFoto = fileStorageService.guardar(dto.getFoto());
        Familiar familiar = personaMapper.toFamiliar(dto, nombreFoto, grupo);

        return personaMapper.toResponse(familiarRepository.save(familiar), null);
    }

    @Override
    @Transactional
    public void darDeBaja(Long personaId) {
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Persona", personaId));
        // Baja LOGICA: se conserva el historial de accesos y de pagos; solo deja de poder ingresar.
        persona.setActivo(false);
    }
}
