package com.sparkadmin.app.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * ============================================================================
 * RegistroDTO (Data Transfer Object)
 * ============================================================================
 * ¿Por qué usar un DTO en lugar de enlazar directamente el formulario a la
 * entidad Persona?
 *
 *   1) Separación de responsabilidades: la entidad Persona representa cómo
 *      se guardan los datos en la base de datos (incluye campos como
 *      "intentosFallidos" o "cuentaBloqueada" que NO deben poder ser
 *      completados/manipulados por el usuario desde un formulario HTML).
 *      El DTO, en cambio, representa exactamente los datos que el
 *      formulario de registro debe capturar: nada más, nada menos.
 *
 *   2) Seguridad: si enlazáramos el formulario directamente a la entidad,
 *      un usuario malicioso podría intentar enviar campos adicionales
 *      (por ejemplo, "cuentaBloqueada=false" o un "id" arbitrario) y,
 *      dependiendo de la configuración, alterar datos que no debería poder
 *      tocar (ataque de tipo "mass assignment"). El DTO actúa como una
 *      "lista blanca" de campos permitidos.
 *
 *   3) Validaciones específicas del formulario: aquí se agregan anotaciones
 *      de Bean Validation (Jakarta Validation) que se verifican
 *      automáticamente cuando el controlador recibe el objeto anotado con
 *      @Valid, ANTES de que la lógica de negocio se ejecute.
 *
 * Cada anotación de validación funciona así: si el valor del campo no
 * cumple la condición, Spring agrega un error al objeto BindingResult /
 * Errors asociado, y el controlador puede decidir qué hacer (normalmente,
 * volver a mostrar el formulario con los mensajes de error).
 * ============================================================================
 */
public class RegistroDTO {

    /**
     * @NotBlank: el valor no puede ser null, ni cadena vacía (""), ni
     * contener solo espacios en blanco (" "). Es más estricto que @NotNull.
     */
    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 30, message = "El documento no puede superar los 30 caracteres")
    private String documento;

    /**
     * @NotNull: a diferencia de @NotBlank (pensada para String), este campo
     * es de tipo LocalDate, por lo que solo tiene sentido validar que no
     * sea nulo (no existe el concepto de "fecha en blanco").
     *
     * @Past: obliga a que la fecha sea estrictamente anterior a la fecha
     * actual del sistema; es decir, no se puede registrar una fecha de
     * nacimiento futura.
     */
    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser una fecha pasada")
    private LocalDate fechaNacimiento;

    /**
     * @Email: valida que el texto tenga una estructura de correo electrónico
     * válida (usuario@dominio.tld). Se combina con @NotBlank porque @Email
     * por sí sola SÍ permite valores nulos o vacíos (se considera "válido"
     * un campo vacío desde el punto de vista de @Email).
     */
    @NotBlank(message = "El correo personal es obligatorio")
    @Email(message = "Debe ingresar un correo electrónico con formato válido")
    @Size(max = 150, message = "El correo no puede superar los 150 caracteres")
    private String correoPersonal;

    /**
     * @Size(min = 6, ...): exige una longitud mínima razonable para la
     * contraseña en texto plano que el usuario escribe en el formulario.
     * IMPORTANTE: este valor viaja en texto plano SOLO durante el envío del
     * formulario (protegido por HTTPS en un entorno real) y es
     * inmediatamente encriptado con BCrypt en la capa de servicio antes de
     * guardarse; nunca se persiste ni se muestra en texto plano.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres")
    private String password;

    /**
     * Campo de confirmación de contraseña. Solo se usa a nivel de
     * formulario (no se persiste en la base de datos) para verificar que el
     * usuario no haya cometido un error de tipeo al definir su contraseña.
     * La comparación entre "password" y "confirmarPassword" se realiza en
     * el controlador/servicio, ya que Bean Validation estándar no compara
     * fácilmente dos campos entre sí sin una anotación personalizada.
     */
    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmarPassword;

    // Constructor vacío requerido para que Spring pueda instanciar el DTO
    // y completarlo automáticamente con los datos del formulario (data
    // binding) antes de pasarlo al método del controlador.
    public RegistroDTO() {
    }

    // ==========================================================================
    // GETTERS Y SETTERS
    // ==========================================================================
    // Imprescindibles para que Thymeleaf (th:field="*{...}") y Spring MVC
    // (@ModelAttribute) puedan leer y escribir los valores del formulario
    // sobre este objeto mediante la convención JavaBean.
    // ==========================================================================

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getCorreoPersonal() {
        return correoPersonal;
    }

    public void setCorreoPersonal(String correoPersonal) {
        this.correoPersonal = correoPersonal;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
