package com.colegio.sistemaescolar.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO del formulario "Cambiar contraseña".
 * La coincidencia entre la nueva contraseña y su confirmación se verifica en el servicio.
 */
@Getter
@Setter
@NoArgsConstructor
public class CambioPasswordDTO {

    @NotBlank(message = "Ingrese su contraseña actual")
    private String passwordActual;

    @NotBlank(message = "Ingrese la nueva contraseña")
    @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",
            message = "La contraseña debe combinar letras y números")
    private String nuevaPassword;

    @NotBlank(message = "Confirme la nueva contraseña")
    private String confirmarPassword;
}
