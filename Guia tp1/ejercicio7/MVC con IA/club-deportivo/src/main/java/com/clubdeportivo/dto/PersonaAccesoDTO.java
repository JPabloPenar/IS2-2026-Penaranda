package com.clubdeportivo.dto;

import com.clubdeportivo.model.TipoAcceso;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de SALIDA para la vista previa de la porteria: muestra la foto y el estado de la persona
 * ANTES de registrar el pasaje, para que el empleado valide visualmente que es quien dice ser.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaAccesoDTO {
    private Long id;
    private String nombreCompleto;
    private String dni;
    private String foto;
    private String tipo;
    private String relacion;
    private boolean activo;
    private boolean alDia;
    /** Texto del ultimo pasaje permitido, p. ej. "Entrada 21/09/2026 10:15" o "Sin registros". */
    private String ultimoAcceso;
    /** Sentido recomendado: SALIDA si la ultima marca fue una ENTRADA, y ENTRADA en otro caso. */
    private TipoAcceso tipoSugerido;
}
