package com.colegio.sistemaescolar.controller;

import com.colegio.sistemaescolar.dto.NotaDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.model.Periodo;
import com.colegio.sistemaescolar.service.AlumnoService;
import com.colegio.sistemaescolar.service.CatalogoService;
import com.colegio.sistemaescolar.service.NotaService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

/** Gestión de calificaciones: consulta con filtros, registro, edición y eliminación. */
@Controller
@RequestMapping("/notas")
@RequiredArgsConstructor
public class NotaController {

    private final NotaService notaService;
    private final AlumnoService alumnoService;
    private final CatalogoService catalogoService;

    /**
     * Listado con filtros opcionales. Un filtro vacío ("Todos") llega como null y significa
     * "no filtrar por ese criterio".
     */
    @GetMapping
    public String listar(@RequestParam(required = false) Long alumnoId,
                         @RequestParam(required = false) Long materiaId,
                         @RequestParam(required = false) Periodo periodo,
                         Model model) {
        model.addAttribute("notas", notaService.filtrar(alumnoId, materiaId, periodo));
        model.addAttribute("alumnos", alumnoService.listar(""));
        model.addAttribute("materias", catalogoService.listarMaterias());
        model.addAttribute("periodos", Periodo.values());
        // Se devuelven los filtros elegidos para que los desplegables conserven la selección
        model.addAttribute("filtroAlumnoId", alumnoId);
        model.addAttribute("filtroMateriaId", materiaId);
        model.addAttribute("filtroPeriodo", periodo);
        return "notas/index";
    }

    @GetMapping("/nueva")
    public String nueva(Model model) {
        model.addAttribute("nota", new NotaDTO());
        cargarListas(model);
        return "notas/formulario";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("nota", notaService.buscarPorId(id));
        cargarListas(model);
        return "notas/formulario";
    }

    /** {@link Principal} identifica al docente que registra la nota (su email). */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("nota") NotaDTO dto,
                          BindingResult resultado,
                          Principal principal,
                          Model model,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            cargarListas(model);
            return "notas/formulario";
        }
        try {
            notaService.guardar(dto, principal.getName());
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            cargarListas(model);
            return "notas/formulario";
        }
        flash.addFlashAttribute("mensajeExito",
                dto.getId() == null ? "La nota se registró correctamente." : "Los cambios de la nota se guardaron.");
        return "redirect:/notas";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        notaService.eliminar(id);
        flash.addFlashAttribute("mensajeExito", "La nota fue eliminada.");
        return "redirect:/notas";
    }

    /** Listas para los desplegables del formulario de notas. */
    private void cargarListas(Model model) {
        model.addAttribute("alumnos", alumnoService.listar(""));
        model.addAttribute("materias", catalogoService.listarMaterias());
        model.addAttribute("periodos", Periodo.values());
    }
}
