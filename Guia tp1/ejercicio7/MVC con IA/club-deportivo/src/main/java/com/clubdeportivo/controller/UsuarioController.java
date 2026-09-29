package com.clubdeportivo.controller;

import com.clubdeportivo.dto.UsuarioRegistroDTO;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.model.Rol;
import com.clubdeportivo.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Administración de empleados. Toda la ruta /admin/** exige rol ADMIN (ver SecurityConfig),
 * así que no hace falta repetir la verificación en cada método.
 */
@Controller
@RequestMapping("/admin/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("usuarios", usuarioService.listar());
        return "admin/usuarios/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("usuario", new UsuarioRegistroDTO());
        return "admin/usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("usuario") UsuarioRegistroDTO dto,
                          BindingResult resultado,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "admin/usuarios/formulario";
        }
        try {
            usuarioService.registrar(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            return "admin/usuarios/formulario";
        }
        flash.addFlashAttribute("mensajeExito", "El usuario se creó correctamente.");
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/{id}/alternar-activo")
    public String alternarActivo(@PathVariable Long id, RedirectAttributes flash) {
        usuarioService.alternarActivo(id);
        flash.addFlashAttribute("mensajeExito", "El estado del usuario se actualizó.");
        return "redirect:/admin/usuarios";
    }
}
