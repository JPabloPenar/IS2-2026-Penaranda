package com.clubdeportivo.controller;

import com.clubdeportivo.dto.FamiliarRegistroDTO;
import com.clubdeportivo.dto.SocioRegistroDTO;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.model.Parentesco;
import com.clubdeportivo.service.CatalogoService;
import com.clubdeportivo.service.SocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Gestión de socios y sus grupos familiares: alta, listado, ficha y baja. */
@Controller
@RequestMapping("/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;
    private final CatalogoService catalogoService;

    @ModelAttribute("parentescos")
    public Parentesco[] parentescos() {
        return Parentesco.values();
    }

    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String busqueda, Model model) {
        model.addAttribute("socios", socioService.listar(busqueda));
        model.addAttribute("busqueda", busqueda == null ? "" : busqueda);
        return "socios/index";
    }

    @GetMapping("/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        model.addAttribute("detalle", socioService.buscarDetalle(id));
        return "socios/detalle";
    }

    @GetMapping("/nuevo")
    public String nuevoSocio(Model model) {
        model.addAttribute("socio", new SocioRegistroDTO());
        return "socios/formulario-socio";
    }

    /**
     * El formulario debe usar enctype="multipart/form-data" para que {@code SocioRegistroDTO.foto}
     * llegue completa. {@code @Valid} evalúa las anotaciones de Jakarta sobre los campos de texto.
     */
    @PostMapping("/guardar")
    public String guardarSocio(@Valid @ModelAttribute("socio") SocioRegistroDTO dto,
                               BindingResult resultado,
                               RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            return "socios/formulario-socio";
        }
        try {
            socioService.registrarSocio(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            return "socios/formulario-socio";
        }
        flash.addFlashAttribute("mensajeExito", "El socio se registró correctamente.");
        return "redirect:/socios";
    }

    @GetMapping("/{socioId}/familiares/nuevo")
    public String nuevoFamiliar(@PathVariable Long socioId, Model model) {
        FamiliarRegistroDTO dto = new FamiliarRegistroDTO();
        dto.setSocioId(socioId);
        model.addAttribute("familiar", dto);
        model.addAttribute("socio", socioService.buscarDetalle(socioId).getSocio());
        return "socios/formulario-familiar";
    }

    @PostMapping("/familiares/guardar")
    public String guardarFamiliar(@Valid @ModelAttribute("familiar") FamiliarRegistroDTO dto,
                                  BindingResult resultado,
                                  Model model,
                                  RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("socio", catalogoService.listarSociosActivos().stream()
                    .filter(s -> s.getId().equals(dto.getSocioId())).findFirst().orElse(null));
            return "socios/formulario-familiar";
        }
        try {
            socioService.registrarFamiliar(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            return "socios/formulario-familiar";
        }
        flash.addFlashAttribute("mensajeExito", "El familiar se agregó correctamente.");
        return "redirect:/socios/" + dto.getSocioId();
    }

    /** Baja por POST (nunca GET) para que un enlace o un rastreador no pueda desactivar a alguien. */
    @PostMapping("/{id}/baja")
    public String darDeBaja(@PathVariable Long id, RedirectAttributes flash) {
        socioService.darDeBaja(id);
        flash.addFlashAttribute("mensajeExito", "La persona fue dada de baja.");
        return "redirect:/socios";
    }
}
