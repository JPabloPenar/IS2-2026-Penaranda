package com.clubdeportivo.dto;

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
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

/**
 * DTO base con los datos que se piden al registrar cualquier persona (socio o familiar).
 *
 * <p>Un DTO (Data Transfer Object) transporta datos entre la vista y el servicio sin exponer la
 * entidad JPA: el formulario solo puede llenar estos campos y no, por ejemplo, el estado "activo".
 * Las anotaciones de Jakarta Validation se evalúan cuando el controlador usa {@code @Valid}.
 */
@Getter
@Setter
@NoArgsConstructor
public class PersonaRegistroDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 80, message = "El nombre debe tener entre 2 y 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 80, message = "El apellido debe tener entre 2 y 80 caracteres")
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "^\\d{7,9}$", message = "El DNI debe tener entre 7 y 9 dígitos, sin puntos")
    private String dni;

    /** {@code @DateTimeFormat(ISO.DATE)} recibe "yyyy-MM-dd" desde un input type="date". */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate fechaNacimiento;

    /** Opcional; si se completa debe tener formato de email. */
    @Email(message = "Ingrese un email válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @Pattern(regexp = "^$|^[0-9+()\\- ]{6,20}$", message = "Use de 6 a 20 caracteres: números, +, guion, paréntesis o espacios")
    private String telefono;

    /**
     * Foto del rostro. El formulario debe usar enctype="multipart/form-data".
     * Su presencia, formato y tamaño los valida FileStorageService (no basta con una anotación,
     * porque también se comprueba el contenido real del archivo).
     */
    private MultipartFile foto;
}
