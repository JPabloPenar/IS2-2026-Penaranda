package com.colegio.sistemaescolar.dto;

import com.colegio.sistemaescolar.model.Periodo;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para registrar y mostrar calificaciones.
 * La escala aceptada es de 0 a 10 (ajustable en {@code @DecimalMin}/{@code @DecimalMax}).
 */
@Getter
@Setter
@NoArgsConstructor
public class NotaDTO {

    private Long id;

    @NotNull(message = "Seleccione un alumno")
    private Long alumnoId;

    @NotNull(message = "Seleccione una materia")
    private Long materiaId;

    @NotNull(message = "Seleccione el período")
    private Periodo periodo;

    @NotNull(message = "La calificación es obligatoria")
    @DecimalMin(value = "0.0", message = "La calificación mínima es 0")
    @DecimalMax(value = "10.0", message = "La calificación máxima es 10")
    @Digits(integer = 2, fraction = 2, message = "Use como máximo 2 decimales")
    private BigDecimal valorNumerico;

    @Size(max = 500, message = "Los comentarios no pueden superar los 500 caracteres")
    private String comentarios;

    // ---- Campos de solo lectura (los completa NotaMapper) ----
    private String alumnoNombre;
    private String materiaNombre;
    private String docenteNombre;
    private boolean aprobado;
    private String fechaRegistro;
}
