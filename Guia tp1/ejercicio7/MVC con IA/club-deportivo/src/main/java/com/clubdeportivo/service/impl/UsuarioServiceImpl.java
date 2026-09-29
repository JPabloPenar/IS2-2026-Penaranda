package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.UsuarioRegistroDTO;
import com.clubdeportivo.dto.UsuarioResponseDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.mapper.UsuarioMapper;
import com.clubdeportivo.model.Usuario;
import com.clubdeportivo.repository.UsuarioRepository;
import com.clubdeportivo.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lógica de negocio de los empleados. Solo el rol ADMIN accede a estas operaciones (ver SecurityConfig). */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UsuarioResponseDTO registrar(UsuarioRegistroDTO dto) {
        String username = dto.getUsername().trim();
        if (usuarioRepository.existsByUsernameIgnoreCase(username)) {
            throw new ReglaNegocioException("username", "Ya existe un usuario con ese nombre de usuario");
        }
        String hash = passwordEncoder.encode(dto.getPassword());
        Usuario guardado = usuarioRepository.save(usuarioMapper.toEntity(dto, hash));
        return usuarioMapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(usuarioMapper::toResponse).toList();
    }

    @Override
    @Transactional
    public void alternarActivo(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario", id));
        usuario.setActivo(!usuario.isActivo());
    }
}
