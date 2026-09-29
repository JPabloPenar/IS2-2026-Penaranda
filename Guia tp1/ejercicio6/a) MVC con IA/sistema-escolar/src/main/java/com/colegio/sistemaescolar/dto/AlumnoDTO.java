package com.colegio.sistemaescolar.dto;

import com.colegio.sistemaescolar.model.Genero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * DTO de alumno: sirve para el formulario (entrada) y para las listas (salida).
 *
 * <p>Las relaciones con Grado y Aula se transportan solo por su id ({@code gradoId}, {@code aulaId});
 * los campos {@code nombreCompleto}, {@code gradoNombre} y {@code aulaCodigo} son de solo lectura
 * y los rellena el mapeador para mostrarlos en las tablas.
 */
@Getter
@Setter
@NoArgsConstructor
public class AlumnoDTO {

    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI o matrícula es obligatorio")
    @Pattern(regexp = "^[0-9A-Za-z.\\-]{6,20}$",
            message = "Use de 6 a 20 caracteres (letras, números, punto o guion)")
    private String dni;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotNull(message = "Seleccione el género")
    private Genero genero;

    @NotNull(message = "Seleccione un grado")
    private Long gradoId;

    @NotNull(message = "Seleccione un aula")
    private Long aulaId;

    // ---- Campos de solo lectura (los completa AlumnoMapper) ----
    private String nombreCompleto;
    private String gradoNombre;
    private String aulaCodigo;
}
