package com.clubdeportivo.mapper;

import com.clubdeportivo.dto.UsuarioRegistroDTO;
import com.clubdeportivo.dto.UsuarioResponseDTO;
import com.clubdeportivo.model.Usuario;
import org.springframework.stereotype.Component;

/** Convierte entre {@link Usuario} y sus DTOs. La contraseña llega YA cifrada desde el servicio. */
@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRegistroDTO dto, String passwordHasheada) {
        Usuario u = new Usuario();
        u.setUsername(dto.getUsername().trim());
        u.setNombre(dto.getNombre().trim());
        u.setApellido(dto.getApellido().trim());
        u.setEmail(dto.getEmail().trim().toLowerCase());
        u.setPassword(passwordHasheada);
        u.setRol(dto.getRol());
        return u;
    }

    public UsuarioResponseDTO toResponse(Usuario u) {
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .username(u.getUsername())
                .nombreCompleto(u.getNombre() + " " + u.getApellido())
                .email(u.getEmail())
                .rol(u.getRol())
                .activo(u.isActivo())
                .build();
    }
}
