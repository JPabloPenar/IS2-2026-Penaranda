package com.colegio.sistemaescolar.controller;

import com.colegio.sistemaescolar.service.DocenteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

/**
 * Rutas /docentes/**: protegidas por Spring Security (requieren sesión iniciada).
 * {@code @RequestMapping} a nivel de clase antepone "/docentes" a todas las rutas del controlador.
 */
@Controller
@RequestMapping("/docentes")
@RequiredArgsConstructor
public class DocenteController {

    private final DocenteService docenteService;

    /** Muestra el perfil del docente autenticado. */
    @GetMapping("/perfil")
    public String perfil(Model model, Principal principal) {
        model.addAttribute("docente", docenteService.buscarPorEmail(principal.getName()));
        return "docentes/perfil";
    }
}
