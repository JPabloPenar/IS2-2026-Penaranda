package com.clubdeportivo.dto;

import com.clubdeportivo.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de ENTRADA para que un administrador cree un empleado. */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioRegistroDTO {

    @NotBlank(message = "El usuario es obligatorio")
    @Pattern(regexp = "^[A-Za-z0-9._-]{3,50}$", message = "Use de 3 a 50 caracteres: letras, números, punto, guion o guion bajo")
    private String username;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 80, message = "El nombre no puede superar los 80 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 80, message = "El apellido no puede superar los 80 caracteres")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Ingrese un email válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 60, message = "La contraseña debe tener entre 8 y 60 caracteres")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "La contraseña debe combinar letras y números")
    private String password;

    @NotNull(message = "Seleccione el rol")
    private Rol rol;
}
