package com.sparkadmin.app.config;

import com.sparkadmin.app.service.PersonaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;

/**
 * ============================================================================
 * CustomAuthenticationSuccessHandler
 * ============================================================================
 * Se ejecuta automáticamente cuando un intento de inicio de sesión resulta
 * EXITOSO (correo existente, cuenta no bloqueada y contraseña correcta).
 *
 * Extiende SavedRequestAwareAuthenticationSuccessHandler en lugar de
 * implementar la interfaz AuthenticationSuccessHandler "desde cero", para
 * heredar gratis un comportamiento estándar muy útil de Spring Security:
 * si el usuario había intentado acceder a una URL protegida ANTES de
 * loguearse (por ejemplo, escribió directamente "/dashboard" en el
 * navegador sin estar autenticado), Spring Security lo redirige de vuelta
 * a esa URL original después del login exitoso, en lugar de mandarlo
 * siempre a una URL fija. Solo se le agrega la lógica adicional de
 * reiniciar el contador de intentos fallidos.
 * ============================================================================
 */
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final PersonaService personaService;

    public CustomAuthenticationSuccessHandler(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Método invocado automáticamente por Spring Security justo después de
     * que la autenticación se complete satisfactoriamente.
     *
     * @param request        la petición HTTP del formulario de login
     * @param response       la respuesta HTTP (usada para la redirección)
     * @param authentication objeto que representa al usuario ya autenticado;
     *                       authentication.getName() devuelve el "username"
     *                       usado para loguearse, que en este proyecto es
     *                       el correoPersonal.
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         Authentication authentication) throws IOException, ServletException {

        // Regla de negocio del enunciado: "Si el login es exitoso antes del
        // tercer fallo, el contador de intentosFallidos debe reiniciarse a 0".
        String correoAutenticado = authentication.getName();
        personaService.reiniciarIntentosFallidos(correoAutenticado);

        // Se delega en la implementación heredada el resto del proceso
        // estándar (redirección a la URL original solicitada, o a la URL
        // por defecto configurada en SecurityConfig si no había ninguna
        // solicitud previa guardada).
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
