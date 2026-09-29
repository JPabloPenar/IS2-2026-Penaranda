package com.clubdeportivo.init;

import com.clubdeportivo.model.*;
import com.clubdeportivo.repository.CuotaRepository;
import com.clubdeportivo.repository.GrupoFamiliarRepository;
import com.clubdeportivo.repository.SocioRepository;
import com.clubdeportivo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Genera datos SINTÉTICOS para las pruebas de carga: un usuario de recepción y N socios
 * (cada uno con su grupo familiar y una cuota del mes en curso), la mitad pagada y la otra
 * mitad pendiente, para poder medir tanto accesos permitidos como rechazados.
 *
 * <p>Solo se activa con {@code --spring.profiles.active=carga} (ver {@code application-carga.properties}).
 * NUNCA debe activarse en producción: por eso está en su propio perfil, separado de
 * {@link DataInitializer} (que usa {@code @Profile("!carga")}).
 */
@Slf4j
@Component
@Profile("carga")
@RequiredArgsConstructor
public class CargaDataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final SocioRepository socioRepository;
    private final GrupoFamiliarRepository grupoFamiliarRepository;
    private final CuotaRepository cuotaRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${club.carga.total-socios}")
    private int totalSocios;

    @Value("${club.carga.usuario}")
    private String usuarioCarga;

    @Value("${club.carga.password}")
    private String passwordCarga;

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario recepcion = new Usuario();
            recepcion.setUsername(usuarioCarga);
            recepcion.setNombre("Recepción");
            recepcion.setApellido("Prueba de carga");
            recepcion.setEmail(usuarioCarga + "@club.local");
            recepcion.setPassword(passwordEncoder.encode(passwordCarga));
            recepcion.setRol(Rol.ROLE_ADMIN); // ADMIN para poder ejercitar tambien /pagos/reporte
            usuarioRepository.save(recepcion);
            log.warn("[perfil carga] Usuario de prueba creado: {} / {}", usuarioCarga, passwordCarga);
        }

        if (socioRepository.count() >= totalSocios) {
            log.info("[perfil carga] Ya existen {} socios o más; no se generan datos nuevamente.", totalSocios);
            return;
        }

        LocalDate hoy = LocalDate.now();
        for (int i = 1; i <= totalSocios; i++) {
            // DNIs correlativos y predecibles: los scripts de k6/JMeter los reconstruyen sin un CSV.
            String dni = String.format("%08d", 40000000 + i);

            Socio socio = new Socio();
            socio.setNombre("Socio");
            socio.setApellido("Prueba" + i);
            socio.setDni(dni);
            socio.setFechaNacimiento(LocalDate.of(1990, 1, 1));
            socio.setFechaAlta(hoy);
            socio.setActivo(true);
            socio = socioRepository.save(socio);

            GrupoFamiliar grupo = new GrupoFamiliar();
            grupo.setTitular(socio);
            grupo = grupoFamiliarRepository.save(grupo);
            socio.setGrupoFamiliar(grupo);
            socioRepository.save(socio);

            Cuota cuota = new Cuota();
            cuota.setGrupoFamiliar(grupo);
            cuota.setMesPeriodo(hoy.getMonthValue());
            cuota.setAnioPeriodo(hoy.getYear());
            cuota.setMontoTotal(new BigDecimal("15000.00"));
            cuota.setFechaVencimiento(hoy.withDayOfMonth(10));
            // La mitad de los socios queda AL DÍA (para medir "acceso permitido") y la otra mitad
            // con la cuota vencida (para medir "acceso rechazado"), en partes iguales.
            boolean alDia = i % 2 == 0;
            cuota.setEstadoCuota(alDia ? EstadoCuota.PAGADA : EstadoCuota.VENCIDA);
            if (!alDia) {
                cuota.setFechaVencimiento(hoy.minusDays(5)); // vencida de verdad, no solo por nombre
            }
            cuotaRepository.save(cuota);

            if (i % 100 == 0) {
                log.info("[perfil carga] Generados {}/{} socios...", i, totalSocios);
            }
        }
        log.warn("[perfil carga] Se generaron {} socios de prueba (DNIs 40000001 a {}).",
                totalSocios, String.format("%08d", 40000000 + totalSocios));
    }
}
