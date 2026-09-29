package com.clubdeportivo.dto;

import com.clubdeportivo.model.TipoAcceso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de ENTRADA de la porteria: DNI de la persona y sentido del pasaje. */
@Getter
@Setter
@NoArgsConstructor
public class AccesoRegistroDTO {

    @NotBlank(message = "Ingrese el DNI")
    @Pattern(regexp = "^\\d{7,9}$", message = "El DNI debe tener entre 7 y 9 dígitos, sin puntos")
    private String dni;

    @NotNull(message = "Seleccione entrada o salida")
    private TipoAcceso tipoAcceso;
}
