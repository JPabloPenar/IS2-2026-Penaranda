package com.clubdeportivo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/** DTO de SALIDA con los datos de un socio o familiar para listados y fichas. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonaResponseDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String dni;
    private String email;
    private String telefono;
    private LocalDate fechaNacimiento;
    /** Nombre del archivo de la foto; la vista arma la URL /fotos/{foto}. Null si no tiene. */
    private String foto;
    /** "Socio" o "Familiar". */
    private String tipo;
    /** "Titular" o el parentesco. */
    private String relacion;
    private boolean activo;
    /** Solo se completa en listados de socios; null si no aplica. */
    private Boolean alDia;
}
