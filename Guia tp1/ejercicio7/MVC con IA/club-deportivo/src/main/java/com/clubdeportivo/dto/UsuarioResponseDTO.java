package com.clubdeportivo.dto;

import com.clubdeportivo.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de SALIDA de un empleado. Nunca incluye la contraseña. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String email;
    private Rol rol;
    private boolean activo;
}
