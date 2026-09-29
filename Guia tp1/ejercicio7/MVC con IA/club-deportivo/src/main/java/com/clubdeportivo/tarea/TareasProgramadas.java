package com.clubdeportivo.tarea;

import com.clubdeportivo.repository.CuotaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

/**
 * Tareas de mantenimiento que corren solas, sin intervención de un empleado.
 *
 * <p>{@code @Scheduled(cron = ...)}: Spring ejecuta el método según la expresión cron
 * (segundo minuto hora dia-mes mes dia-semana). Requiere {@code @EnableScheduling} (ver AppConfig).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TareasProgramadas {

    private final CuotaRepository cuotaRepository;
    private final Clock clock;

    /**
     * Todos los días a las 00:05, marca como VENCIDA cualquier cuota PENDIENTE cuyo vencimiento
     * ya pasó. El control de acceso YA funciona sin esta tarea (compara fechas directamente), pero
     * mantener el campo "estadoCuota" al día hace correctos los listados y reportes por estado.
     */
    @Scheduled(cron = "0 5 0 * * *")
    @Transactional
    public void marcarCuotasVencidas() {
        int actualizadas = cuotaRepository.marcarVencidas(LocalDate.now(clock));
        if (actualizadas > 0) {
            log.info("Se marcaron {} cuota(s) como VENCIDA(S)", actualizadas);
        }
    }
}
