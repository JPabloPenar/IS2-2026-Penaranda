package com.sparkadmin.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ============================================================================
 * AuthController
 * ============================================================================
 * Controlador dedicado exclusivamente a mostrar la vista de inicio de
 * sesión. El PROCESAMIENTO real del formulario de login (verificar
 * credenciales, contar intentos fallidos, etc.) NO ocurre aquí: ese
 * trabajo lo intercepta directamente el filtro de Spring Security
 * configurado en SecurityConfig (formLogin().loginProcessingUrl("/login")),
 * por lo que este controlador solo necesita encargarse de renderizar el
 * formulario HTML.
 *
 * @Controller:
 *   Anotación de Spring MVC (especialización de @Component) que marca esta
 *   clase como un "controlador web clásico", es decir, sus métodos
 *   devuelven el NOMBRE de una vista (una plantilla Thymeleaf) que Spring
 *   se encarga de renderizar y enviar como HTML al navegador. Se diferencia
 *   de @RestController, cuyos métodos devuelven directamente datos (JSON,
 *   texto), no vistas.
 * ============================================================================
 */
@Controller
public class AuthController {

    /**
     * Muestra el formulario de inicio de sesión.
     *
     * @GetMapping("/login"):
     *   Le indica a Spring MVC que este método debe ejecutarse cuando
     *   llegue una petición HTTP de tipo GET a la ruta "/login". Esta es
     *   exactamente la misma ruta configurada como loginPage("/login") en
     *   SecurityConfig: cuando un usuario no autenticado intenta acceder a
     *   una página protegida, Spring Security lo redirige automáticamente
     *   aquí.
     *
     * Nota importante: este método NO necesita recibir ni procesar los
     * parámetros de error (errorNoRegistrado, errorBloqueado, etc.) de
     * forma manual en Java. Al ser parámetros de query string de la propia
     * URL ("/login?errorBloqueado=true"), la plantilla login.html puede
     * leerlos directamente mediante la expresión Thymeleaf
     * "${param.errorBloqueado}", sin que el controlador tenga que
     * reenviarlos explícitamente al modelo.
     *
     * @return el nombre lógico de la plantilla a renderizar: Spring
     *         Boot + Thymeleaf resuelven automáticamente "login" hacia el
     *         archivo src/main/resources/templates/login.html
     */
    @GetMapping("/login")
    public String mostrarFormularioLogin() {
        return "login";
    }

    /**
     * Redirige la raíz del sitio ("/") hacia el login. Es una comodidad
     * para que, al entrar a la aplicación sin especificar ninguna ruta,
     * el usuario vea directamente la pantalla de inicio de sesión (o el
     * dashboard, si Spring Security detecta que ya tiene una sesión
     * activa y por lo tanto no lo redirige al login).
     */
    @GetMapping("/")
    public String raiz() {
        return "redirect:/login";
    }
}
