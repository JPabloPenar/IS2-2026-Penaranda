package com.clubdeportivo.service.impl;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoResponseDTO;
import com.clubdeportivo.dto.ReportePagosDTO;
import com.clubdeportivo.dto.TotalMedioDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.mapper.PagoMapper;
import com.clubdeportivo.model.Cuota;
import com.clubdeportivo.model.EstadoCuota;
import com.clubdeportivo.model.Pago;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.PagoRepository;
import com.clubdeportivo.repository.TotalPorMedio;
import com.clubdeportivo.service.PagoService;
import com.clubdeportivo.service.pago.ProcesadorPago;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Lógica de negocio de los pagos. Aplica el patrón <b>Strategy</b>: recibe la lista de todos los
 * {@link ProcesadorPago} (Spring inyecta uno por cada {@code @Component} que implementa la interfaz)
 * y arma un mapa {@code MedioPago -> ProcesadorPago} para elegir el correcto en tiempo de ejecución.
 */
@Service
@Transactional
public class PagoServiceImpl implements PagoService {

    /** Límite de filas del detalle del reporte; los totales siempre son del rango completo. */
    private static final int LIMITE_DETALLE_REPORTE = 500;

    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final Clock clock;
    private final Map<com.clubdeportivo.model.MedioPago, ProcesadorPago> procesadores;

    public PagoServiceImpl(CuotaRepository cuotaRepository,
                           PagoRepository pagoRepository,
                           PagoMapper pagoMapper,
                           Clock clock,
                           List<ProcesadorPago> listaProcesadores) {
        this.cuotaRepository = cuotaRepository;
        this.pagoRepository = pagoRepository;
        this.pagoMapper = pagoMapper;
        this.clock = clock;
        this.procesadores = listaProcesadores.stream()
                .collect(java.util.stream.Collectors.toMap(ProcesadorPago::getMedio, Function.identity()));
    }

    @Override
    public PagoResponseDTO registrarPago(PagoCuotaDTO dto) {
        // 1) Bloqueo pesimista: si dos empleados cobran la MISMA cuota al mismo tiempo, el segundo
        //    espera a que termine el primero y ve el saldo ya actualizado (evita sobre-pago concurrente).
        Cuota cuota = cuotaRepository.buscarParaActualizar(dto.getCuotaId())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cuota", dto.getCuotaId()));

        if (cuota.getEstadoCuota() == EstadoCuota.PAGADA) {
            throw new ReglaNegocioException("cuotaId", "Esta cuota ya está totalmente pagada");
        }

        // 2) Reglas propias del medio de pago (Strategy)
        ProcesadorPago procesador = procesadores.get(dto.getMedioPago());
        if (procesador == null) {
            throw new ReglaNegocioException("medioPago", "Medio de pago no soportado");
        }
        procesador.validar(dto);

        // 3) Regla general: no se puede pagar más del saldo pendiente
        BigDecimal pagadoPrevio = pagoRepository.sumarMontoPorCuota(cuota.getId());
        if (pagadoPrevio == null) {
            pagadoPrevio = BigDecimal.ZERO;
        }
        BigDecimal saldo = cuota.getMontoTotal().subtract(pagadoPrevio);
        if (dto.getMontoPagado().compareTo(saldo) > 0) {
            throw new ReglaNegocioException("montoPagado",
                    "El monto supera el saldo pendiente de la cuota ($" + saldo + ")");
        }

        // 4) Se registra el pago
        Pago pago = new Pago();
        pago.setCuota(cuota);
        pago.setMontoPagado(dto.getMontoPagado());
        pago.setFechaPago(LocalDateTime.now(clock));
        pago.setMedioPago(dto.getMedioPago());
        pago.setComprobanteReferencia(dto.getComprobanteReferencia() == null
                ? null : dto.getComprobanteReferencia().trim());
        pago = pagoRepository.save(pago);

        // 5) Se recalcula el estado de la cuota con lo YA guardado (evita usar un total obsoleto)
        BigDecimal totalPagado = pagadoPrevio.add(dto.getMontoPagado());
        if (totalPagado.compareTo(cuota.getMontoTotal()) >= 0) {
            cuota.setEstadoCuota(EstadoCuota.PAGADA);
        }
        // Si queda saldo, el estado sigue PENDIENTE (o VENCIDA: la tarea programada la corrige si aplica).

        return pagoMapper.toResponse(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public ReportePagosDTO reporte(LocalDate desde, LocalDate hasta) {
        if (hasta.isBefore(desde)) {
            throw new ReglaNegocioException("hasta", "La fecha 'hasta' no puede ser anterior a 'desde'");
        }
        LocalDateTime inicio = desde.atStartOfDay();
        LocalDateTime fin = hasta.plusDays(1).atStartOfDay(); // "hasta" incluido: se corta al empezar el día siguiente

        List<Pago> pagos = pagoRepository.reporte(inicio, fin, PageRequest.of(0, LIMITE_DETALLE_REPORTE));
        List<TotalPorMedio> totalesCrudos = pagoRepository.totalesPorMedio(inicio, fin);

        List<TotalMedioDTO> totales = totalesCrudos.stream()
                .map(t -> new TotalMedioDTO(t.getMedioPago(), t.getTotal(), t.getCantidad()))
                .toList();
        BigDecimal totalGeneral = totales.stream().map(TotalMedioDTO::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long cantidadTotal = totales.stream().mapToLong(TotalMedioDTO::getCantidad).sum();

        return ReportePagosDTO.builder()
                .desde(desde)
                .hasta(hasta)
                .pagos(pagos.stream().map(pagoMapper::toResponse).toList())
                .totales(totales)
                .totalGeneral(totalGeneral)
                .cantidadTotal(cantidadTotal)
                .detalleLimitado(cantidadTotal > LIMITE_DETALLE_REPORTE)
                .build();
    }
}
