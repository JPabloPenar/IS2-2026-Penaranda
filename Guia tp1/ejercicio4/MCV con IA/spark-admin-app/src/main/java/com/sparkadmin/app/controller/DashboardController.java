package com.sparkadmin.app.controller;

import com.sparkadmin.app.model.Persona;
import com.sparkadmin.app.service.PersonaService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * ============================================================================
 * DashboardController
 * ============================================================================
 * Controlador de la vista PROTEGIDA principal de la aplicación: el
 * dashboard/home que solo puede ver un usuario ya autenticado.
 *
 * ¿Por qué esta ruta está protegida sin que este controlador tenga que
 * verificar nada manualmente (por ejemplo, sin un "if (usuario == null)")?
 *   Porque en SecurityConfig se configuró .anyRequest().authenticated()
 *   para cualquier ruta que no esté explícitamente en la lista de rutas
 *   públicas. Como "/dashboard" NO está en esa lista, Spring Security
 *   intercepta la petición ANTES de que llegue a este controlador: si no
 *   hay una sesión autenticada, redirige automáticamente a "/login"; si SÍ
 *   la hay, deja pasar la petición con la certeza de que, dentro del
 *   método, siempre habrá un usuario autenticado disponible.
 * ============================================================================
 */
@Controller
public class DashboardController {

    private final PersonaService personaService;

    public DashboardController(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Muestra el panel principal (dashboard) con los datos de la persona
     * actualmente autenticada.
     *
     * Authentication:
     *   Spring MVC permite declarar este tipo como parámetro de método y
     *   automáticamente inyecta el objeto de autenticación asociado a la
     *   petición HTTP actual (equivalente a llamar a
     *   SecurityContextHolder.getContext().getAuthentication(), pero de
     *   forma mucho más limpia y declarativa). authentication.getName()
     *   devuelve el "username" con el que el usuario inició sesión, que en
     *   este proyecto es su correoPersonal.
     *
     * @param authentication información del usuario autenticado, inyectada
     *                        automáticamente por Spring Security
     * @param model modelo para pasar los datos de la Persona a la vista
     * @return el nombre lógico de la vista del dashboard
     */
    @GetMapping("/dashboard")
    public String mostrarDashboard(Authentication authentication, Model model) {

        // Se obtiene el correo del usuario autenticado a partir del
        // objeto Authentication que gestiona Spring Security.
        String correoAutenticado = authentication.getName();

        // Se recupera la entidad Persona completa desde la base de datos
        // para poder mostrar en pantalla todos sus datos personales
        // (nombre, apellido, documento, fecha de nacimiento), no solo el
        // correo con el que se identificó.
        //
        // .orElseThrow(...): en este punto, la existencia de la Persona
        // está garantizada (si Spring Security autenticó exitosamente al
        // usuario, es porque PersonaDetailsService la encontró momentos
        // antes); aun así, se maneja el caso "imposible" de forma segura
        // en lugar de arriesgarse a un NullPointerException.
        Persona persona = personaService.buscarPorCorreo(correoAutenticado)
                .orElseThrow(() -> new IllegalStateException(
                        "Usuario autenticado no encontrado en la base de datos: " + correoAutenticado));

        // Se agrega la Persona al modelo bajo el nombre "persona", que es
        // la variable que la plantilla dashboard.html usará para mostrar
        // la información de bienvenida (${persona.nombre}, etc.).
        model.addAttribute("persona", persona);

        return "dashboard";
    }
}
