package com.clubdeportivo.service;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.dto.PagoResponseDTO;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.mapper.PagoMapper;
import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.PagoRepository;
import com.clubdeportivo.service.impl.PagoServiceImpl;
import com.clubdeportivo.service.pago.PagoEfectivoProcesador;
import com.clubdeportivo.service.pago.PagoMercadoPagoProcesador;
import com.clubdeportivo.service.pago.PagoTransferenciaProcesador;
import com.clubdeportivo.service.pago.ProcesadorPago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link PagoServiceImpl}.
 *
 * <p>{@code @ExtendWith(MockitoExtension.class)}: activa las anotaciones {@code @Mock} de Mockito.
 * No se levanta el contexto de Spring (test 100% unitario, rápido): los repositorios y el mapper
 * son "dobles de prueba" (mocks) cuyo comportamiento se define con {@code when(...)}.
 *
 * <p>Se usa un {@link Clock} FIJO ({@code Clock.fixed}) para que "ahora" sea siempre el mismo
 * instante y las aserciones sobre fechas sean deterministas.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PagoService: registro de pagos en efectivo, transferencia y Mercado Pago")
class PagoServiceTest {

    @Mock
    private CuotaRepository cuotaRepository;
    @Mock
    private PagoRepository pagoRepository;

    private final PagoMapper pagoMapper = new PagoMapper();
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-21T10:00:00Z"), ZoneId.of("UTC"));

    private PagoServiceImpl pagoService;
    private Cuota cuota;

    @BeforeEach
    void setUp() {
        // Se instancian los TRES procesadores reales (son clases pequeñas, sin sentido "mockearlas")
        // para probar que PagoServiceImpl elige el correcto según el medio de pago (patrón Strategy).
        List<ProcesadorPago> procesadores = List.of(
                new PagoEfectivoProcesador(),
                new PagoTransferenciaProcesador(pagoRepository),
                new PagoMercadoPagoProcesador(pagoRepository));
        pagoService = new PagoServiceImpl(cuotaRepository, pagoRepository, pagoMapper, clock, procesadores);

        Socio titular = new Socio();
        titular.setNombre("Ana");
        titular.setApellido("Pérez");
        titular.setDni("30111222");

        GrupoFamiliar grupo = new GrupoFamiliar();
        grupo.setTitular(titular);

        cuota = new Cuota();
        cuota.setGrupoFamiliar(grupo);
        cuota.setMesPeriodo(9);
        cuota.setAnioPeriodo(2026);
        cuota.setMontoTotal(new BigDecimal("15000.00"));
        cuota.setEstadoCuota(EstadoCuota.PENDIENTE);
        cuota.setFechaVencimiento(LocalDate.of(2026, 9, 10));

        when(cuotaRepository.buscarParaActualizar(1L)).thenReturn(Optional.of(cuota));
        // Por defecto, la cuota no tiene pagos previos; cada test puede sobrescribirlo si lo necesita.
        lenient().when(pagoRepository.sumarMontoPorCuota(any())).thenReturn(null);
        lenient().when(pagoRepository.save(any(Pago.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    @DisplayName("Pago en EFECTIVO")
    class Efectivo {

        @Test
        @DisplayName("un pago por el monto total marca la cuota como PAGADA")
        void pagoCompletoMarcaCuotaComoPagada() {
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.EFECTIVO);

            PagoResponseDTO respuesta = pagoService.registrarPago(dto);

            assertThat(cuota.getEstadoCuota()).isEqualTo(EstadoCuota.PAGADA);
            assertThat(respuesta.getMedioPago()).isEqualTo(MedioPago.EFECTIVO);
            assertThat(respuesta.getMontoPagado()).isEqualByComparingTo("15000.00");

            ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
            verify(pagoRepository).save(captor.capture());
            assertThat(captor.getValue().getFechaPago()).isEqualTo(LocalDateTime.now(clock));
        }

        @Test
        @DisplayName("un pago parcial deja la cuota PENDIENTE")
        void pagoParcialDejaCuotaPendiente() {
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("5000.00"));
            dto.setMedioPago(MedioPago.EFECTIVO);

            pagoService.registrarPago(dto);

            assertThat(cuota.getEstadoCuota()).isEqualTo(EstadoCuota.PENDIENTE);
        }

        @Test
        @DisplayName("no permite cobrar un monto mayor al saldo pendiente")
        void rechazaMontoMayorAlSaldo() {
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("99999.00"));
            dto.setMedioPago(MedioPago.EFECTIVO);

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("saldo pendiente");

            verify(pagoRepository, never()).save(any());
        }

        @Test
        @DisplayName("no permite cobrar una cuota que ya está totalmente pagada")
        void rechazaCuotaYaPagada() {
            cuota.setEstadoCuota(EstadoCuota.PAGADA);
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("100.00"));
            dto.setMedioPago(MedioPago.EFECTIVO);

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("totalmente pagada");
        }
    }

    @Nested
    @DisplayName("Pago por TRANSFERENCIA")
    class Transferencia {

        @Test
        @DisplayName("exige número de operación")
        void exigeComprobante() {
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.TRANSFERENCIA);
            dto.setComprobanteReferencia("  ");

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("número de operación");
        }

        @Test
        @DisplayName("rechaza un número de operación ya usado")
        void rechazaComprobanteDuplicado() {
            when(pagoRepository.existsByMedioPagoAndComprobanteReferencia(MedioPago.TRANSFERENCIA, "OP-123"))
                    .thenReturn(true);

            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.TRANSFERENCIA);
            dto.setComprobanteReferencia("OP-123");

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("ya fue registrado");
        }

        @Test
        @DisplayName("con un comprobante válido, marca la cuota como PAGADA")
        void comprobanteValidoMarcaCuotaComoPagada() {
            when(pagoRepository.existsByMedioPagoAndComprobanteReferencia(eq(MedioPago.TRANSFERENCIA), any()))
                    .thenReturn(false);

            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.TRANSFERENCIA);
            dto.setComprobanteReferencia("OP-456");

            PagoResponseDTO respuesta = pagoService.registrarPago(dto);

            assertThat(cuota.getEstadoCuota()).isEqualTo(EstadoCuota.PAGADA);
            assertThat(respuesta.getComprobanteReferencia()).isEqualTo("OP-456");
        }
    }

    @Nested
    @DisplayName("Pago por MERCADO_PAGO")
    class MercadoPago {

        @Test
        @DisplayName("exige el ID de pago de Mercado Pago")
        void exigeIdDePago() {
            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.MERCADO_PAGO);

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("ID de pago");
        }

        @Test
        @DisplayName("rechaza un ID de pago ya usado")
        void rechazaIdDuplicado() {
            when(pagoRepository.existsByMedioPagoAndComprobanteReferencia(MedioPago.MERCADO_PAGO, "MP-999"))
                    .thenReturn(true);

            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("15000.00"));
            dto.setMedioPago(MedioPago.MERCADO_PAGO);
            dto.setComprobanteReferencia("MP-999");

            assertThatThrownBy(() -> pagoService.registrarPago(dto))
                    .isInstanceOf(ReglaNegocioException.class)
                    .hasMessageContaining("ya fue registrado");
        }

        @Test
        @DisplayName("con un ID válido, registra el pago con el medio MERCADO_PAGO")
        void idValidoRegistraPago() {
            when(pagoRepository.existsByMedioPagoAndComprobanteReferencia(eq(MedioPago.MERCADO_PAGO), any()))
                    .thenReturn(false);

            PagoCuotaDTO dto = new PagoCuotaDTO();
            dto.setCuotaId(1L);
            dto.setMontoPagado(new BigDecimal("7500.00"));
            dto.setMedioPago(MedioPago.MERCADO_PAGO);
            dto.setComprobanteReferencia("MP-777");

            PagoResponseDTO respuesta = pagoService.registrarPago(dto);

            assertThat(respuesta.getMedioPago()).isEqualTo(MedioPago.MERCADO_PAGO);
            assertThat(cuota.getEstadoCuota()).isEqualTo(EstadoCuota.PENDIENTE); // pago parcial: 7500 de 15000
        }
    }
}
