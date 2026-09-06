package com.sparkadmin.app.controller;

import com.sparkadmin.app.dto.RegistroDTO;
import com.sparkadmin.app.service.PersonaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * ============================================================================
 * RegistroController
 * ============================================================================
 * Controlador responsable de todo el flujo de alta de nuevos usuarios:
 * mostrar el formulario de registro (GET) y procesar su envío (POST).
 *
 * @Controller: ver explicación detallada en AuthController.
 * ============================================================================
 */
@Controller
public class RegistroController {

    /**
     * Se inyecta la interfaz de servicio (no la implementación concreta),
     * respetando el principio de "programar contra interfaces" explicado
     * en PersonaService.
     */
    private final PersonaService personaService;

    public RegistroController(PersonaService personaService) {
        this.personaService = personaService;
    }

    /**
     * Muestra el formulario de registro (petición GET).
     *
     * @ModelAttribute mecanismo alternativo: aquí se opta por agregar el
     * objeto directamente al Model (ver más abajo), en lugar de recibirlo
     * como parámetro de método con @ModelAttribute, porque en esta
     * petición GET el objeto está completamente vacío (recién creado) y
     * solo sirve para que Thymeleaf pueda enlazar los campos del
     * formulario mediante th:object="${registroDTO}" sin lanzar un error
     * de "variable no encontrada".
     *
     * @param model el "modelo" es el mapa de datos que el controlador pasa
     *              a la vista para que esta pueda renderizarlos
     *              dinámicamente. Model es una abstracción de Spring MVC
     *              que se traduce, para el motor Thymeleaf, en variables
     *              accesibles con ${...}.
     * @return el nombre lógico de la vista de registro
     */
    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        // Se agrega un RegistroDTO vacío al modelo bajo el nombre
        // "registroDTO", que es el mismo nombre que la plantilla
        // registro.html usará en th:object="${registroDTO}" para enlazar
        // los campos del formulario.
        model.addAttribute("registroDTO", new RegistroDTO());
        return "registro";
    }

    /**
     * Procesa el envío del formulario de registro (petición POST).
     *
     * @ModelAttribute("registroDTO"):
     *   Le indica a Spring MVC que debe tomar TODOS los parámetros
     *   enviados por el formulario HTML (name="nombre", name="apellido",
     *   etc.) y usarlos para poblar automáticamente una nueva instancia de
     *   RegistroDTO (usando sus setters), exponiéndola además en el
     *   modelo bajo el nombre "registroDTO" (el mismo nombre que usa la
     *   vista), de forma que si hay que volver a mostrar el formulario con
     *   errores, los valores ya tipeados por el usuario NO se pierdan.
     *
     * @Valid:
     *   Le indica a Spring que, inmediatamente después de poblar el
     *   RegistroDTO con los datos del formulario, ejecute automáticamente
     *   todas las validaciones de Bean Validation declaradas en esa clase
     *   (@NotBlank, @Email, @Size, @Past, etc.). Los resultados de esa
     *   validación (qué campos fallaron y por qué) se depositan en el
     *   parámetro BindingResult que debe declararse INMEDIATAMENTE
     *   después del parámetro anotado con @Valid (es una regla estricta de
     *   Spring MVC: si no van uno justo después del otro, Spring lanza una
     *   excepción en lugar de simplemente completar el BindingResult).
     *
     * BindingResult:
     *   Objeto que contiene el resultado de la validación. Si
     *   bindingResult.hasErrors() es true, significa que al menos un
     *   campo no cumplió sus validaciones, y el flujo normal es volver a
     *   mostrar el mismo formulario (para que el usuario corrija los
     *   datos) en lugar de continuar con el registro.
     *
     * @param model modelo para pasar mensajes adicionales a la vista
     * @return el nombre de la vista a mostrar a continuación: si hay
     *         errores, se vuelve a mostrar "registro"; si todo es
     *         correcto, se redirige a "/login" con un mensaje de éxito.
     */
    @PostMapping("/registro")
    public String procesarRegistro(@ModelAttribute("registroDTO") @Valid RegistroDTO registroDTO,
                                    BindingResult bindingResult,
                                    Model model) {

        // -----------------------------------------------------------
        // PASO 1: verificar los errores de validación "automáticos"
        // (los declarados con anotaciones en RegistroDTO).
        // -----------------------------------------------------------
        if (bindingResult.hasErrors()) {
            // Se retorna nuevamente a la vista de registro. Gracias a
            // @ModelAttribute, Thymeleaf ya tiene acceso tanto al objeto
            // "registroDTO" (con los valores que el usuario ya había
            // completado) como a los errores de cada campo, que se
            // muestran usando th:errors="*{campo}" en la plantilla.
            return "registro";
        }

        // -----------------------------------------------------------
        // PASO 2: validaciones de negocio ADICIONALES que Bean Validation
        // no puede expresar con anotaciones simples (comparar dos campos
        // entre sí, o consultar la base de datos para detectar
        // duplicados).
        // -----------------------------------------------------------

        // 2.a) Verificar que la contraseña y su confirmación coincidan.
        if (registroDTO.getPassword() != null
                && !registroDTO.getPassword().equals(registroDTO.getConfirmarPassword())) {
            bindingResult.rejectValue("confirmarPassword", "error.registroDTO",
                    "Las contraseñas ingresadas no coinciden");
        }

        // 2.b) Verificar que no exista ya una cuenta con ese correo.
        if (personaService.existeCorreo(registroDTO.getCorreoPersonal())) {
            bindingResult.rejectValue("correoPersonal", "error.registroDTO",
                    "Ya existe una cuenta registrada con ese correo electrónico");
        }

        // 2.c) Verificar que no exista ya una cuenta con ese documento.
        if (personaService.existeDocumento(registroDTO.getDocumento())) {
            bindingResult.rejectValue("documento", "error.registroDTO",
                    "Ya existe una cuenta registrada con ese número de documento");
        }

        // Si alguna de las validaciones de negocio anteriores agregó un
        // error, se vuelve a mostrar el formulario, ahora también con
        // estos mensajes adicionales.
        if (bindingResult.hasErrors()) {
            return "registro";
        }

        // -----------------------------------------------------------
        // PASO 3: todos los datos son válidos -> delegar en la capa de
        // servicio la creación real del nuevo usuario (incluyendo la
        // encriptación de la contraseña con BCrypt).
        // -----------------------------------------------------------
        personaService.registrarNuevaPersona(registroDTO);

        // -----------------------------------------------------------
        // PASO 4: redirigir al login con un indicador de éxito en la URL.
        // -----------------------------------------------------------
        // Se usa el prefijo "redirect:" para forzar una redirección HTTP
        // real (patrón Post-Redirect-Get) en lugar de simplemente
        // renderizar la vista de login desde este mismo método. Esto
        // evita el clásico problema de "reenvío de formulario" si el
        // usuario refresca la página después de registrarse, y además
        // hace que la URL final sea "/login?registroExitoso=true", que
        // login.html puede leer con ${param.registroExitoso} para mostrar
        // un mensaje de bienvenida.
        return "redirect:/login?registroExitoso=true";
    }
}
