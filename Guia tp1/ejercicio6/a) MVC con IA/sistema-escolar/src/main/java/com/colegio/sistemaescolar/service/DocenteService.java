package com.colegio.sistemaescolar.service;

import com.colegio.sistemaescolar.dto.CambioPasswordDTO;
import com.colegio.sistemaescolar.dto.DocenteRegistroDTO;
import com.colegio.sistemaescolar.dto.DocenteResponseDTO;

/**
 * Contrato de la lógica de negocio de los docentes/usuarios.
 * Trabajar contra la interfaz desacopla los controladores de la implementación concreta.
 */
public interface DocenteService {

    /** Registra un docente nuevo (contraseña cifrada) y dispara el correo de bienvenida. */
    DocenteResponseDTO registrar(DocenteRegistroDTO dto);

    /** Devuelve los datos públicos del docente con ese email. */
    DocenteResponseDTO buscarPorEmail(String email);

    /** Cambia la contraseña del docente autenticado verificando la actual. */
    void cambiarPassword(String email, CambioPasswordDTO dto);
}
