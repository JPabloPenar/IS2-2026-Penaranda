package com.colegio.sistemaescolar.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO de solo lectura para poblar listas desplegables de materias. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
}
