package com.clubdeportivo.service;

import com.clubdeportivo.dto.FamiliarRegistroDTO;
import com.clubdeportivo.dto.PersonaResponseDTO;
import com.clubdeportivo.dto.SocioDetalleDTO;
import com.clubdeportivo.dto.SocioRegistroDTO;

import java.util.List;

/** Contrato de la lógica de negocio de socios, familiares y grupos familiares. */
public interface SocioService {

    /** Lista socios (con su estado de cuota) filtrando por nombre, apellido o DNI. */
    List<PersonaResponseDTO> listar(String busqueda);

    /** Ficha completa: datos del socio, sus familiares y sus cuotas. */
    SocioDetalleDTO buscarDetalle(Long socioId);

    /** Da de alta un socio: crea la persona, su grupo familiar y, si trae foto, la almacena. */
    PersonaResponseDTO registrarSocio(SocioRegistroDTO dto);

    /** Suma un familiar al grupo de un socio existente. */
    PersonaResponseDTO registrarFamiliar(FamiliarRegistroDTO dto);

    /** Da de baja (lógica) a una persona; no borra su historial de accesos ni de pagos. */
    void darDeBaja(Long personaId);
}
