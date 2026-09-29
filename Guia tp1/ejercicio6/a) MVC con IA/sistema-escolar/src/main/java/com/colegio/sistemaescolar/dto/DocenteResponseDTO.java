package com.colegio.sistemaescolar.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO de SALIDA con los datos públicos de un docente. Nunca incluye la contraseña ni su hash.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocenteResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private String nombreCompleto;
    private String email;
    private String sexo;
    private LocalDate fechaNacimiento;
    private String rol;
    /** Fecha de alta ya formateada como texto (dd/MM/yyyy). */
    private String fechaRegistro;
}
