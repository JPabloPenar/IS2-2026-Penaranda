package com.colegio.sistemaescolar.controller;

import com.colegio.sistemaescolar.dto.AlumnoDTO;
import com.colegio.sistemaescolar.exception.ReglaNegocioException;
import com.colegio.sistemaescolar.model.Genero;
import com.colegio.sistemaescolar.service.AlumnoService;
import com.colegio.sistemaescolar.service.CatalogoService;
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

/** Gestión de alumnos: listado con búsqueda, alta, edición y baja. */
@Controller
@RequestMapping("/alumnos")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final CatalogoService catalogoService;

    /** Listado. {@code @RequestParam} lee ?q=texto de la URL; es opcional. */
    @GetMapping
    public String listar(@RequestParam(name = "q", required = false) String busqueda, Model model) {
        model.addAttribute("alumnos", alumnoService.listar(busqueda));
        model.addAttribute("busqueda", busqueda == null ? "" : busqueda);
        return "alumnos/index";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("alumno", new AlumnoDTO());
        cargarCatalogos(model);
        return "alumnos/formulario";
    }

    /** {@code @PathVariable} toma el {id} de la ruta /alumnos/editar/5. */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        cargarCatalogos(model);
        return "alumnos/formulario";
    }

    /** Crea o actualiza según el DTO traiga o no un id. */
    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("alumno") AlumnoDTO dto,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            cargarCatalogos(model);
            return "alumnos/formulario";
        }
        try {
            alumnoService.guardar(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            cargarCatalogos(model);
            return "alumnos/formulario";
        }
        flash.addFlashAttribute("mensajeExito",
                dto.getId() == null ? "El alumno se guardó correctamente." : "Los cambios del alumno se guardaron.");
        return "redirect:/alumnos";
    }

    /** La baja es un POST (nunca un GET) para que un enlace o un rastreador no pueda borrar datos. */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes flash) {
        alumnoService.eliminar(id);
        flash.addFlashAttribute("mensajeExito", "El alumno y sus notas fueron eliminados.");
        return "redirect:/alumnos";
    }

    /** Agrega al modelo las listas que necesitan los desplegables del formulario. */
    private void cargarCatalogos(Model model) {
        model.addAttribute("generos", Genero.values());
        model.addAttribute("grados", catalogoService.listarGrados());
        model.addAttribute("aulas", catalogoService.listarAulas());
    }
}
