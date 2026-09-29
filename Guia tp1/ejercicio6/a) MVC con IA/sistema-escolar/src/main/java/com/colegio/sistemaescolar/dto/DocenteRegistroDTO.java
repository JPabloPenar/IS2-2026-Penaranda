package com.colegio.sistemaescolar.dto;

import com.colegio.sistemaescolar.model.Genero;
import jakarta.validation.constraints.Email;
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
 * DTO de ENTRADA para el formulario de registro de un docente.
 *
 * <p>Un DTO (Data Transfer Object) transporta datos entre la vista y el servicio sin exponer
 * la entidad JPA. Aquí, además, evita que el formulario pueda "inyectar" campos internos como
 * el rol o el estado activo. Las anotaciones de Jakarta Validation se evalúan cuando el
 * controlador declara el parámetro con {@code @Valid}.
 */
@Getter
@Setter
@NoArgsConstructor
public class DocenteRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres")
    private String apellido;

    @NotNull(message = "Seleccione el sexo")
    private Genero sexo;

    /** {@code @DateTimeFormat(ISO.DATE)} permite recibir "yyyy-MM-dd" desde input type="date". */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Ingrese un email válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe combinar letras y números")
    private String password;

    @NotBlank(message = "Confirme la contraseña")
    private String confirmarPassword;
}
