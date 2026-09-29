package com.clubdeportivo.repository;

import com.clubdeportivo.model.MedioPago;

import java.math.BigDecimal;

/**
 * Proyeccion (interface-based projection) del resultado agrupado del reporte. Spring Data crea una
 * implementacion que lee cada alias de la consulta (medioPago, total, cantidad).
 */
public interface TotalPorMedio {
    MedioPago getMedioPago();

    BigDecimal getTotal();

    Long getCantidad();
}
