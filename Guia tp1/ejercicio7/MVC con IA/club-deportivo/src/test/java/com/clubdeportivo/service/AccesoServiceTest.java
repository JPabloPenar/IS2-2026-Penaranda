package com.clubdeportivo.service;

import com.clubdeportivo.dto.AccesoRegistroDTO;
import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.ResultadoAccesoDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.mapper.AccesoMapper;
import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.PersonaRepository;
import com.clubdeportivo.repository.RegistroAccesoRepository;
import com.clubdeportivo.service.impl.AccesoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias de {@link AccesoServiceImpl}: la regla central del control de acceso
 * (solo se ingresa con la cuota al día) y el registro de entrada/salida.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AccesoService: validación de ingreso/egreso según el estado de la cuota")
class AccesoServiceTest {

    @Mock
    private PersonaRepository personaRepository;
    @Mock
    private CuotaRepository cuotaRepository;
    @Mock
    private RegistroAccesoRepository registroAccesoRepository;

    private final AccesoMapper accesoMapper = new AccesoMapper();
    private final Clock clock = Clock.fixed(Instant.parse("2026-09-21T10:00:00Z"), ZoneId.of("UTC"));

    private AccesoServiceImpl accesoService;
    private Socio socio;
    private GrupoFamiliar grupo;

    @BeforeEach
    void setUp() throws Exception {
        accesoService = new AccesoServiceImpl(personaRepository, cuotaRepository, registroAccesoRepository, accesoMapper, clock);

        socio = new Socio();
        socio.setNombre("Ana");
        socio.setApellido("Pérez");
        socio.setDni("30111222");
        socio.setActivo(true);

        grupo = new GrupoFamiliar();
        grupo.setTitular(socio);
        setId(grupo, 100L);
        socio.setGrupoFamiliar(grupo);
        setId(socio, 1L);

        lenient().when(registroAccesoRepository.save(any(RegistroAcceso.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    /**
     * BaseAuditableEntity no expone un setId público; se usa reflexión solo para preparar los tests.
     * Sube por la jerarquía de clases hasta encontrar el campo "id" (declarado en BaseAuditableEntity),
     * sin asumir cuántos niveles de herencia tiene la entidad concreta (Socio hereda de Persona,
     * GrupoFamiliar hereda directo de BaseAuditableEntity).
     */
    private void setId(Object entidad, Long id) throws Exception {
        Class<?> clase = entidad.getClass();
        while (clase != null) {
            try {
                Field campo = clase.getDeclaredField("id");
                campo.setAccessible(true);
                campo.set(entidad, id);
                return;
            } catch (NoSuchFieldException e) {
                clase = clase.getSuperclass();
            }
        }
        throw new NoSuchFieldException("No se encontró el campo 'id' en la jerarquía de " + entidad.getClass());
    }

    @Test
    @DisplayName("con la cuota al día, la ENTRADA se permite")
    void entradaConCuotaAlDiaSePermite() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(cuotaRepository.contarAdeudadas(100L, LocalDate.now(clock))).thenReturn(0L);

        AccesoRegistroDTO dto = new AccesoRegistroDTO();
        dto.setDni("30111222");
        dto.setTipoAcceso(TipoAcceso.ENTRADA);

        ResultadoAccesoDTO resultado = accesoService.registrar(dto);

        assertThat(resultado.isPermitido()).isTrue();
        assertThat(resultado.getMensaje()).contains("Cuota al día");

        ArgumentCaptor<RegistroAcceso> captor = ArgumentCaptor.forClass(RegistroAcceso.class);
        verify(registroAccesoRepository).save(captor.capture());
        assertThat(captor.getValue().isPermitido()).isTrue();
        assertThat(captor.getValue().getTipoAcceso()).isEqualTo(TipoAcceso.ENTRADA);
        assertThat(captor.getValue().getFechaHora()).isEqualTo(LocalDateTime.now(clock));
    }

    @Test
    @DisplayName("con una cuota adeudada, la ENTRADA se rechaza")
    void entradaConCuotaAdeudadaSeRechaza() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(cuotaRepository.contarAdeudadas(100L, LocalDate.now(clock))).thenReturn(1L);

        AccesoRegistroDTO dto = new AccesoRegistroDTO();
        dto.setDni("30111222");
        dto.setTipoAcceso(TipoAcceso.ENTRADA);

        ResultadoAccesoDTO resultado = accesoService.registrar(dto);

        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.getMensaje()).contains("Cuota adeudada");

        ArgumentCaptor<RegistroAcceso> captor = ArgumentCaptor.forClass(RegistroAcceso.class);
        verify(registroAccesoRepository).save(captor.capture());
        assertThat(captor.getValue().isPermitido()).isFalse(); // el intento rechazado TAMBIÉN se audita
    }

    @Test
    @DisplayName("la SALIDA siempre se permite, incluso con cuota adeudada")
    void salidaSiempreSePermite() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(socio));

        AccesoRegistroDTO dto = new AccesoRegistroDTO();
        dto.setDni("30111222");
        dto.setTipoAcceso(TipoAcceso.SALIDA);

        ResultadoAccesoDTO resultado = accesoService.registrar(dto);

        assertThat(resultado.isPermitido()).isTrue();
        // La salida no necesita consultar el estado de la cuota (regla de negocio explícita).
        verify(cuotaRepository, never()).contarAdeudadas(any(), any());
    }

    @Test
    @DisplayName("una persona dada de baja no puede ingresar aunque esté al día")
    void personaDadaDeBajaNoIngresa() {
        socio.setActivo(false);
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(socio));

        AccesoRegistroDTO dto = new AccesoRegistroDTO();
        dto.setDni("30111222");
        dto.setTipoAcceso(TipoAcceso.ENTRADA);

        ResultadoAccesoDTO resultado = accesoService.registrar(dto);

        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.getMensaje()).contains("dada de baja");
    }

    @Test
    @DisplayName("un DNI inexistente lanza RecursoNoEncontradoException")
    void dniInexistenteLanzaExcepcion() {
        when(personaRepository.findByDni("00000000")).thenReturn(Optional.empty());

        AccesoRegistroDTO dto = new AccesoRegistroDTO();
        dto.setDni("00000000");
        dto.setTipoAcceso(TipoAcceso.ENTRADA);

        assertThatThrownBy(() -> accesoService.registrar(dto))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(registroAccesoRepository, never()).save(any());
    }

    @Test
    @DisplayName("buscarParaAcceso sugiere SALIDA si el último pasaje permitido fue una ENTRADA")
    void sugiereSalidaTrasUnaEntrada() {
        when(personaRepository.findByDni("30111222")).thenReturn(Optional.of(socio));
        when(cuotaRepository.contarAdeudadas(100L, LocalDate.now(clock))).thenReturn(0L);

        RegistroAcceso ultimaEntrada = new RegistroAcceso();
        ultimaEntrada.setTipoAcceso(TipoAcceso.ENTRADA);
        ultimaEntrada.setFechaHora(LocalDateTime.now(clock).minusHours(1));
        when(registroAccesoRepository.findFirstByPersonaIdAndPermitidoTrueOrderByFechaHoraDesc(1L))
                .thenReturn(Optional.of(ultimaEntrada));

        PersonaAccesoDTO resultado = accesoService.buscarParaAcceso("30111222");

        assertThat(resultado.getTipoSugerido()).isEqualTo(TipoAcceso.SALIDA);
        assertThat(resultado.isAlDia()).isTrue();
    }
}
