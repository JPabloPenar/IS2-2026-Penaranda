package com.clubdeportivo.dto;

import com.clubdeportivo.model.Parentesco;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de alta de un familiar: datos de la persona + socio titular y parentesco. */
@Getter
@Setter
@NoArgsConstructor
public class FamiliarRegistroDTO extends PersonaRegistroDTO {

    /** Id del socio titular al que se suma el familiar. */
    @NotNull(message = "Falta indicar el socio titular")
    private Long socioId;

    @NotNull(message = "Seleccione el parentesco")
    private Parentesco parentesco;
}
