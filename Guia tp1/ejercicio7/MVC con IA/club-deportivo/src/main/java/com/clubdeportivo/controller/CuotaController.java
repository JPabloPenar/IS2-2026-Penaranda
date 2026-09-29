package com.clubdeportivo.controller;

import com.clubdeportivo.dto.CuotaDTO;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.model.EstadoCuota;
import com.clubdeportivo.service.CatalogoService;
import com.clubdeportivo.service.CuotaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Emisión y consulta de cuotas. */
@Controller
@RequestMapping("/cuotas")
@RequiredArgsConstructor
public class CuotaController {

    private final CuotaService cuotaService;
    private final CatalogoService catalogoService;

    @ModelAttribute("estados")
    public EstadoCuota[] estados() {
        return EstadoCuota.values();
    }

    @GetMapping
    public String listar(@RequestParam(required = false) EstadoCuota estado, Model model) {
        model.addAttribute("cuotas", cuotaService.listar(estado));
        model.addAttribute("filtroEstado", estado);
        return "cuotas/index";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("cuota", new CuotaDTO());
        model.addAttribute("socios", catalogoService.listarSociosActivos());
        return "cuotas/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cuota") CuotaDTO dto,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("socios", catalogoService.listarSociosActivos());
            return "cuotas/formulario";
        }
        try {
            cuotaService.emitir(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            model.addAttribute("socios", catalogoService.listarSociosActivos());
            return "cuotas/formulario";
        }
        flash.addFlashAttribute("mensajeExito", "La cuota se emitió correctamente.");
        return "redirect:/cuotas";
    }
}
