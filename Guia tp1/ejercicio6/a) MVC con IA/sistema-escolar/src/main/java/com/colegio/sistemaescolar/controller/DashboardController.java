package com.colegio.sistemaescolar.controller;

import com.colegio.sistemaescolar.service.AlumnoService;
import com.colegio.sistemaescolar.service.CatalogoService;
import com.colegio.sistemaescolar.service.DocenteService;
import com.colegio.sistemaescolar.service.NotaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/** Panel principal: resumen de la actividad del colegio para el docente que inició sesión. */
@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DocenteService docenteService;
    private final AlumnoService alumnoService;
    private final NotaService notaService;
    private final CatalogoService catalogoService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        model.addAttribute("docente", docenteService.buscarPorEmail(principal.getName()));
        model.addAttribute("totalAlumnos", alumnoService.contar());
        model.addAttribute("totalMaterias", catalogoService.contarMaterias());
        model.addAttribute("totalNotas", notaService.contar());
        model.addAttribute("promedio", notaService.promedioGeneral());
        model.addAttribute("ultimasNotas", notaService.ultimasNotas());
        return "dashboard";
    }
}
