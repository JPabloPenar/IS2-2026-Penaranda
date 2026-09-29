package com.colegio.sistemaescolar.service.impl;

import com.colegio.sistemaescolar.dto.CambioPasswordDTO;
import com.colegio.sistemaescolar.dto.DocenteRegistroDTO;
import com.colegio.sistemaescolar.dto.DocenteResponseDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.mapper.DocenteMapper;
import com.colegio.sistemaescolar.model.Docente;
import com.colegio.sistemaescolar.repository.DocenteRepository;
import com.colegio.sistemaescolar.service.DocenteService;
import com.colegio.sistemaescolar.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lógica de negocio de docentes/usuarios: registro y cambio de contraseña.
 *
 * <p>{@code @Transactional} en un método abre una transacción de base de datos: si ocurre una
 * excepción, todos los cambios se revierten (rollback); si termina bien, se confirman (commit).
 */
@Service
@RequiredArgsConstructor
public class DocenteServiceImpl implements DocenteService {

    private final DocenteRepository docenteRepository;
    private final DocenteMapper docenteMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public DocenteResponseDTO registrar(DocenteRegistroDTO dto) {
        String email = dto.getEmail().trim().toLowerCase();

        // Regla 1: el email (username) debe ser único
        if (docenteRepository.existsByEmail(email)) {
            throw new ReglaNegocioException("email", "Ya existe una cuenta registrada con este email");
        }
        // Regla 2: contraseña y confirmación deben coincidir
        if (!dto.getPassword().equals(dto.getConfirmarPassword())) {
            throw new ReglaNegocioException("confirmarPassword", "Las contraseñas no coinciden");
        }

        // La contraseña se cifra con BCrypt ANTES de tocar la entidad
        String hash = passwordEncoder.encode(dto.getPassword());
        Docente guardado = docenteRepository.save(docenteMapper.toEntity(dto, hash));

        // Envío asíncrono: retorna de inmediato y el correo sale en otro hilo
        emailService.enviarBienvenida(guardado.getEmail(), guardado.getNombre());

        return docenteMapper.toResponse(guardado);
    }

    /** {@code readOnly = true} indica a Hibernate que no habrá cambios y permite optimizar. */
    @Override
    @Transactional(readOnly = true)
    public DocenteResponseDTO buscarPorEmail(String email) {
        Docente docente = docenteRepository.findByEmail(email)
                .orElseThrow(() -> new ReglaNegocioException("email", "No existe el docente indicado"));
        return docenteMapper.toResponse(docente);
    }

    @Override
    @Transactional
    public void cambiarPassword(String email, CambioPasswordDTO dto) {
        Docente docente = docenteRepository.findByEmail(email)
                .orElseThrow(() -> new ReglaNegocioException("passwordActual", "No se encontró tu cuenta"));

        // La contraseña actual se compara contra el hash guardado (matches maneja la sal de BCrypt)
        if (!passwordEncoder.matches(dto.getPasswordActual(), docente.getPassword())) {
            throw new ReglaNegocioException("passwordActual", "La contraseña actual no es correcta");
        }
        if (!dto.getNuevaPassword().equals(dto.getConfirmarPassword())) {
            throw new ReglaNegocioException("confirmarPassword", "Las contraseñas no coinciden");
        }
        if (passwordEncoder.matches(dto.getNuevaPassword(), docente.getPassword())) {
            throw new ReglaNegocioException("nuevaPassword", "La nueva contraseña debe ser distinta a la actual");
        }

        // No hace falta llamar a save(): la entidad es "gestionada" y Hibernate detecta el cambio
        // (dirty checking) y ejecuta el UPDATE al confirmar la transacción.
        docente.setPassword(passwordEncoder.encode(dto.getNuevaPassword()));
    }
}
