package com.clubdeportivo.service;

import com.clubdeportivo.dto.UsuarioRegistroDTO;
import com.clubdeportivo.dto.UsuarioResponseDTO;

import java.util.List;

/** Contrato de la lógica de negocio de los empleados (usuarios del sistema). */
public interface UsuarioService {

    UsuarioResponseDTO registrar(UsuarioRegistroDTO dto);

    List<UsuarioResponseDTO> listar();

    /** Alterna activo/inactivo. Un usuario inactivo no puede iniciar sesión (ver SecurityConfig). */
    void alternarActivo(Long id);
}
