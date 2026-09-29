package com.clubdeportivo.controller;

import com.clubdeportivo.dto.PagoCuotaDTO;
import com.clubdeportivo.exception.ReglaNegocioException;
import com.clubdeportivo.model.MedioPago;
import com.clubdeportivo.service.CuotaService;
import com.clubdeportivo.service.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

/** Cobro de cuotas y reporte de pagos. */
@Controller
@RequestMapping("/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;
    private final CuotaService cuotaService;

    @ModelAttribute("medios")
    public MedioPago[] medios() {
        return MedioPago.values();
    }

    /** Formulario de cobro para una cuota puntual (llega desde el listado de cuotas). */
    @GetMapping("/cobrar/{cuotaId}")
    public String formularioCobro(@org.springframework.web.bind.annotation.PathVariable Long cuotaId, Model model) {
        PagoCuotaDTO dto = new PagoCuotaDTO();
        dto.setCuotaId(cuotaId);
        model.addAttribute("pago", dto);
        model.addAttribute("cuota", cuotaService.buscarPorId(cuotaId));
        return "pagos/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("pago") PagoCuotaDTO dto,
                          BindingResult resultado,
                          Model model,
                          RedirectAttributes flash) {
        if (resultado.hasErrors()) {
            model.addAttribute("cuota", cuotaService.buscarPorId(dto.getCuotaId()));
            return "pagos/formulario";
        }
        try {
            pagoService.registrarPago(dto);
        } catch (ReglaNegocioException e) {
            resultado.rejectValue(e.getCampo(), "regla.negocio", e.getMessage());
            model.addAttribute("cuota", cuotaService.buscarPorId(dto.getCuotaId()));
            return "pagos/formulario";
        }
        flash.addFlashAttribute("mensajeExito", "El pago se registró correctamente.");
        return "redirect:/cuotas";
    }

    /**
     * Reporte de recaudación. {@code @PreAuthorize}: seguridad A NIVEL DE MÉTODO (además de la de URL
     * en SecurityConfig); solo el ADMIN ve cuánto se recaudó y por qué medio.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/reporte")
    public String reporte(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                          Model model) {
        // Sin filtro explícito, se muestra el mes en curso (rango útil por defecto).
        LocalDate hoy = LocalDate.now();
        LocalDate desdeEfectivo = desde != null ? desde : hoy.withDayOfMonth(1);
        LocalDate hastaEfectivo = hasta != null ? hasta : hoy;

        model.addAttribute("reporte", pagoService.reporte(desdeEfectivo, hastaEfectivo));
        return "pagos/reporte";
    }
}
