package com.clubdeportivo.controller;

import com.clubdeportivo.dto.AccesoRegistroDTO;
import com.clubdeportivo.dto.PersonaAccesoDTO;
import com.clubdeportivo.dto.ResultadoAccesoDTO;
import com.clubdeportivo.exception.RecursoNoEncontradoException;
import com.clubdeportivo.model.TipoAcceso;
import com.clubdeportivo.service.AccesoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Pantalla de portería: control rápido de entrada/salida. Muestra la foto de la persona ANTES
 * de confirmar el pasaje, para que el empleado la compare visualmente contra quien tiene enfrente.
 */
@Controller
@RequiredArgsConstructor
public class AccesoController {

    private final AccesoService accesoService;

    @ModelAttribute("tiposAcceso")
    public TipoAcceso[] tiposAcceso() {
        return TipoAcceso.values();
    }

    @GetMapping("/accesos")
    public String panel(Model model) {
        model.addAttribute("acceso", new AccesoRegistroDTO());
        model.addAttribute("ultimosAccesos", accesoService.ultimosAccesos());
        return "accesos/index";
    }

    /**
     * Paso 1: el empleado escribe el DNI y el sistema muestra la ficha (foto, si está al día,
     * último pasaje) SIN registrar todavía nada.
     */
    @GetMapping("/accesos/buscar")
    public String buscar(@RequestParam String dni, Model model, RedirectAttributes flash) {
        try {
            PersonaAccesoDTO persona = accesoService.buscarParaAcceso(dni);
            model.addAttribute("persona", persona);
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("errorBusqueda", e.getMessage());
        }
        model.addAttribute("acceso", new AccesoRegistroDTO());
        model.addAttribute("ultimosAccesos", accesoService.ultimosAccesos());
        return "accesos/index";
    }

    /** Paso 2: confirma el pasaje (entrada o salida) ya validado visualmente por el empleado. */
    @PostMapping("/accesos/registrar")
    public String registrar(@Valid @ModelAttribute("acceso") AccesoRegistroDTO dto,
                            BindingResult resultado,
                            RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            flash.addFlashAttribute("errorBusqueda", "Datos de acceso inválidos");
            return "redirect:/accesos";
        }
        try {
            ResultadoAccesoDTO resultadoAcceso = accesoService.registrar(dto);
            flash.addFlashAttribute("resultadoAcceso", resultadoAcceso);
        } catch (RecursoNoEncontradoException e) {
            flash.addFlashAttribute("errorBusqueda", e.getMessage());
        }
        return "redirect:/accesos";
    }
}
