package com.colegio.sistemaescolar.controller;

import com.colegio.sistemaescolar.dto.CambioPasswordDTO;
import com.colegio.sistemaescolar.dto.DocenteRegistroDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.model.Genero;
import com.colegio.sistemaescolar.service.DocenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/**
 * Controlador de autenticación: login, registro y cambio de contraseña.
 *
 * <p>{@code @Controller}: componente de la capa Controlador de MVC. Cada método devuelve el nombre
 * de una vista Thymeleaf (o un "redirect:"). El POST de /login lo procesa Spring Security, no este
 * controlador; aquí solo se muestra el formulario.
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

    private final DocenteService docenteService;

    /**
     * {@code @ModelAttribute} a nivel de método: el valor devuelto se agrega automáticamente
     * al modelo de TODAS las vistas de este controlador con el nombre "generos".
     */
    @ModelAttribute("generos")
    public Genero[] generos() {
        return Genero.values();
    }

    /** Raíz del sitio: redirige al panel (si no hay sesión, Security enviará al login). */
    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // ------------------------------- REGISTRO -------------------------------

    @GetMapping("/registro")
    public String mostrarRegistro(Model model) {
        // El formulario necesita un DTO vacío para enlazar los campos con th:object
        model.addAttribute("docente", new DocenteRegistroDTO());
        return "auth/registro";
    }

    /**
     * Procesa el formulario de registro.
     *
     * <p>{@code @Valid} ejecuta las validaciones de Jakarta sobre el DTO y deja los errores en
     * {@link BindingResult}, que debe ir INMEDIATAMENTE después del objeto validado.
     * {@code @ModelAttribute("docente")} enlaza los campos del formulario con el DTO.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("docente") DocenteRegistroDTO dto,
                                   BindingResult resultado,
                                   RedirectAttributes flash) {
        // Validación cruzada: contraseña y confirmación (solo si no hay ya un error en esos campos)
        if (dto.getPassword() != null
                && !resultado.hasFieldErrors("confirmarPassword")
                && !dto.getPassword().equals(dto.getConfirmarPassword())) {
            resultado.rejectValue("confirmarPassword", "password.noCoincide", "Las contraseñas no coinciden");
        }
        if (resultado.hasErrors()) {
            return "auth/registro"; // Se vuelve a mostrar el formulario con los errores
        }

        try {
            docenteService.registrar(dto);
        } catch (ReglaNegocioException e) {
            // Se asocia el error de negocio (p. ej. email duplicado) al campo correspondiente
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            return "auth/registro";
        }

        // Patrón Post-Redirect-Get: evita reenviar el formulario al recargar la página.
        // Los "flash attributes" sobreviven a UNA redirección.
        flash.addFlashAttribute("mensajeExito",
                "Tu cuenta fue creada. Inicia sesión para continuar.");
        return "redirect:/login";
    }

    // --------------------------- CAMBIO DE CONTRASEÑA ---------------------------

    @GetMapping("/cambiar-password")
    public String mostrarCambioPassword(Model model) {
        model.addAttribute("cambioPassword", new CambioPasswordDTO());
        return "auth/cambiar-password";
    }

    /**
     * {@link Principal} lo inyecta Spring MVC con el usuario autenticado; su nombre es el email.
     * Así el docente solo puede cambiar SU contraseña: el email nunca viaja en el formulario.
     */
    @PostMapping("/cambiar-password")
    public String procesarCambioPassword(@Valid @ModelAttribute("cambioPassword") CambioPasswordDTO dto,
                                         BindingResult resultado,
                                         Principal principal,
                                         RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "auth/cambiar-password";
        }
        try {
            docenteService.cambiarPassword(principal.getName(), dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            return "auth/cambiar-password";
        }
        flash.addFlashAttribute("mensajeExito", "Tu contraseña se actualizó correctamente.");
        return "redirect:/dashboard";
    }
}
