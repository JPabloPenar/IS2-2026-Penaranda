package com.clubdeportivo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador de autenticación. El POST de /login lo procesa Spring Security (ver SecurityConfig),
 * no este controlador; aquí solo se muestra el formulario.
 */
@Controller
public class AuthController {

    @GetMapping("/")
    public String raiz() {
        return "redirect:/accesos"; // La portería es la pantalla principal del sistema
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
