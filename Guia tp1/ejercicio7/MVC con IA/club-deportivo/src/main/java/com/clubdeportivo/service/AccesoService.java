package com.clubdeportivo.service;

import com.clubdeportivo.dto.AccesoRegistroDTO;
import com.clubdeportivo.dto.AccesoResponseDTO;
import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.ResultadoAccesoDTO;

import java.util.List;

/** Contrato de la lógica de negocio del control de acceso (molinete de portería). */
public interface AccesoService {

    /** Busca a la persona por DNI para la vista previa de portería (foto, estado, cuota, último pasaje). */
    PersonaAccesoDTO buscarParaAcceso(String dni);

    /**
     * Registra un intento de entrada o salida. SIEMPRE queda un registro, sea permitido o
     * rechazado, para poder auditar los intentos fallidos.
     */
    ResultadoAccesoDTO registrar(AccesoRegistroDTO dto);

    /** Los últimos pasajes registrados, para el panel de portería. */
    List<AccesoResponseDTO> ultimosAccesos();
}
