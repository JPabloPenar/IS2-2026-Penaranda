package com.clubdeportivo.service;

import com.clubdeportivo.dto.PersonaResponseDTO;

import java.util.List;

/** Listados de apoyo para los formularios (selects de socios, etc.). */
public interface CatalogoService {

    /** Todos los socios activos, para elegir el titular en los formularios de familiar y cuota. */
    List<PersonaResponseDTO> listarSociosActivos();
}
