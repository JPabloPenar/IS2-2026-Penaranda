package com.sparkadmin.app.config;

import com.sparkadmin.app.model.Persona;
import com.sparkadmin.app.service.PersonaService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * ============================================================================
 * CustomAuthenticationFailureHandler
 * ============================================================================
 * Implementa AuthenticationFailureHandler, la interfaz que Spring Security
 * invoca automáticamente CADA VEZ que un intento de inicio de sesión
 * (formLogin) falla, sea cual sea el motivo del fallo.
 *
 * ¿Por qué se necesita un manejador personalizado en lugar de usar el
 * comportamiento por defecto de Spring Security?
 *   Porque el enunciado exige distinguir TRES escenarios de fallo
 *   diferentes, cada uno con su propio mensaje al usuario:
 *     1) El correo ingresado no existe en el sistema -> invitar a
 *        registrarse.
 *     2) El correo existe pero la contraseña es incorrecta -> se debe
 *        contar el intento fallido y, si se llega a 3, bloquear la cuenta.
 *     3) La cuenta ya está bloqueada -> impedir el acceso con un mensaje
 *        específico, SIN siquiera intentar validar la contraseña otra vez.
 *
 *   Spring Security, por defecto, simplemente redirige a "/login?error"
 *   con un mensaje genérico. Este manejador personalizado inspecciona el
 *   TIPO concreto de AuthenticationException recibida para decidir a qué
 *   parámetro de la URL de login redirigir, y así la vista (login.html)
 *   puede mostrar el bloque de alerta correspondiente.
 * ============================================================================
 */
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    /**
     * Se inyecta el servicio de negocio para poder registrar el intento
     * fallido (incrementar contador / bloquear cuenta) en el momento
     * exacto en que se detecta una contraseña incorrecta.
     */
    private final PersonaService personaService;

    public CustomAuthenticationFailureHandler(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Método invocado automáticamente por el filtro de autenticación de
     * Spring Security (UsernamePasswordAuthenticationFilter) cuando el
     * proceso de login falla por cualquier motivo.
     *
     * @param request   la petición HTTP original del formulario de login
     * @param response  la respuesta HTTP que se va a enviar (se usa para
     *                  hacer la redirección)
     * @param exception la excepción concreta lanzada durante el intento de
     *                  autenticación; su tipo exacto es lo que nos permite
     *                  distinguir el motivo del fallo
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                         HttpServletResponse response,
                                         AuthenticationException exception) throws IOException, ServletException {

        // Se recupera el correo tal como lo escribió el usuario en el
        // formulario, para poder: (a) buscarlo/actualizarlo en la capa de
        // negocio y (b) reenviarlo de vuelta a la vista de login para que
        // el campo de correo no se muestre vacío tras el error.
        String correoIngresado = request.getParameter("username");

        String urlRedireccion;

        if (exception instanceof UsernameNotFoundException) {
            // -----------------------------------------------------------
            // CASO 1: el correo ingresado NO existe en el sistema.
            // -----------------------------------------------------------
            // Esta excepción solo llega hasta aquí "sin disfrazar" porque
            // en SecurityConfig configuramos explícitamente el
            // DaoAuthenticationProvider con
            // setHideUserNotFoundExceptions(false); de lo contrario, Spring
            // Security la convierte automáticamente en una
            // BadCredentialsException genérica (por razones de seguridad,
            // para no revelar en general si un correo existe o no). En
            // este proyecto, el propio enunciado pide mostrar un mensaje
            // explícito invitando al registro, por lo que decidimos
            // conscientemente exponer esta información.
            urlRedireccion = "/login?errorNoRegistrado=true&email=" + codificar(correoIngresado);

        } else if (exception instanceof LockedException) {
            // -----------------------------------------------------------
            // CASO 2: la cuenta YA estaba bloqueada antes de este intento.
            // -----------------------------------------------------------
            // Esta excepción la lanza automáticamente Spring Security
            // ANTES de comparar la contraseña, en cuanto detecta que el
            // UserDetails devuelto por PersonaDetailsService tiene
            // accountLocked = true. Por lo tanto, aquí NO corresponde
            // incrementar el contador de intentos fallidos otra vez (la
            // cuenta ya está bloqueada), solo informar al usuario.
            urlRedireccion = "/login?errorBloqueado=true&email=" + codificar(correoIngresado);

        } else if (exception instanceof BadCredentialsException) {
            // -----------------------------------------------------------
            // CASO 3: el correo existe, pero la contraseña es incorrecta.
            // -----------------------------------------------------------
            // Aquí es donde se aplica la regla de negocio central del
            // enunciado: se delega en el servicio el incremento del
            // contador de intentos fallidos y, si corresponde, el bloqueo
            // automático de la cuenta.
            Persona personaActualizada = personaService.registrarIntentoFallido(correoIngresado);

            if (Boolean.TRUE.equals(personaActualizada.getCuentaBloqueada())) {
                // Este intento fallido fue exactamente el que hizo que la
                // cuenta llegara a 3 intentos fallidos: se informa de
                // inmediato el bloqueo con el mensaje exigido por el
                // enunciado.
                urlRedireccion = "/login?errorBloqueado=true&email=" + codificar(correoIngresado);
            } else {
                // Todavía quedan intentos disponibles: se informa cuántos
                // intentos fallidos lleva acumulados, para que la vista
                // pueda mostrar, por ejemplo, "Le quedan 2 intentos".
                urlRedireccion = "/login?errorCredenciales=true&intentos="
                        + personaActualizada.getIntentosFallidos()
                        + "&email=" + codificar(correoIngresado);
            }

        } else {
            // -----------------------------------------------------------
            // CASO GENÉRICO: cualquier otra excepción de autenticación no
            // contemplada explícitamente arriba (por robustez del sistema).
            // -----------------------------------------------------------
            urlRedireccion = "/login?error=true";
        }

        // Se realiza una redirección HTTP (302) hacia la propia página de
        // login con los parámetros calculados. La vista login.html lee
        // estos parámetros de la URL (a través de la expresión Thymeleaf
        // ${param.NOMBRE}) para decidir qué bloque de alerta mostrar.
        response.sendRedirect(urlRedireccion);
    }

    /**
     * Codifica el correo para poder incluirlo de forma segura como
     * parámetro dentro de una URL (por ejemplo, escapando el símbolo "@"),
     * evitando así una URL mal formada o vulnerabilidades de inyección en
     * la URL de redirección.
     */
    private String codificar(String valor) {
        if (valor == null) {
            return "";
        }
        return URLEncoder.encode(valor, StandardCharsets.UTF_8);
    }
}
