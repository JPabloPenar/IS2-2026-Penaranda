package com.sparkadmin.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * ============================================================================
 * Persona (Entidad JPA)
 * ============================================================================
 * Esta clase representa, al mismo tiempo, a la "Persona" (datos personales)
 * y al "Usuario" del sistema (credenciales de acceso), tal como lo pide el
 * enunciado. Es la clase central del modelo de dominio.
 *
 * ¿Por qué está en el paquete "model" (a veces llamado "entity")?
 *   Porque es responsabilidad exclusiva de esta capa representar la
 *   estructura de los datos persistentes del negocio: NO contiene lógica de
 *   negocio (eso vive en la capa "service"), ni sabe nada de HTTP (eso vive
 *   en "controller"), simplemente describe "qué forma tiene" una Persona y
 *   cómo se mapea a una tabla de base de datos.
 *
 * @Entity:
 *   Anotación de JPA (Jakarta Persistence API) que marca esta clase como una
 *   "entidad persistente": Hibernate (la implementación de JPA que usa Spring
 *   Boot por defecto) la reconoce y genera/gestiona automáticamente una tabla
 *   de base de datos asociada a ella.
 *
 * @Table(name = "personas"):
 *   Especifica explícitamente el nombre de la tabla en la base de datos.
 *   Si se omite, Hibernate usaría por defecto el nombre de la clase
 *   ("persona"), pero es una buena práctica ser explícitos.
 * ============================================================================
 */
@Entity
@Table(name = "personas")
public class Persona {

    /**
     * Identificador único (clave primaria) de la tabla.
     *
     * @Id: marca este campo como la clave primaria de la entidad.
     * @GeneratedValue(strategy = GenerationType.IDENTITY): delega en la
     *   propia base de datos la generación del valor (columna auto-incremental,
     *   típico de MySQL/H2). Cada vez que se inserta una nueva Persona, la
     *   base de datos le asigna automáticamente el siguiente número disponible.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de pila de la persona.
     * @Column(nullable = false) -> a nivel de base de datos, esta columna
     * no admite valores NULL (restricción de integridad además de la
     * validación @NotBlank que se hace a nivel de DTO/formulario).
     */
    @Column(nullable = false, length = 100)
    private String nombre;

    /** Apellido de la persona. */
    @Column(nullable = false, length = 100)
    private String apellido;

    /**
     * Número de documento de identidad.
     * unique = true -> la base de datos garantiza que no pueda haber dos
     * personas registradas con el mismo número de documento.
     */
    @Column(nullable = false, unique = true, length = 30)
    private String documento;

    /**
     * Fecha de nacimiento de la persona.
     * Se usa el tipo moderno java.time.LocalDate (API de fechas de Java 8+),
     * que representa una fecha sin hora ni zona horaria, ideal para este caso.
     */
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    /**
     * Correo personal de la persona.
     * Este campo cumple una doble función en el sistema:
     *   1) Dato de contacto de la persona.
     *   2) "username" (nombre de usuario) que se utiliza para iniciar sesión
     *      en el sistema, tal como exige el enunciado.
     * unique = true -> no puede haber dos cuentas con el mismo correo,
     * porque de lo contrario Spring Security no podría identificar de forma
     * unívoca con qué usuario autenticar.
     */
    @Column(nullable = false, unique = true, length = 150)
    private String correoPersonal;

    /**
     * Contraseña de acceso, almacenada SIEMPRE encriptada (hash BCrypt), NUNCA
     * en texto plano. El proceso de encriptación ocurre en la capa de
     * servicio (PersonaServiceImpl) usando un PasswordEncoder de Spring
     * Security antes de guardar la entidad en la base de datos.
     *
     * length = 100: un hash BCrypt típico ocupa 60 caracteres, se deja
     * margen adicional por seguridad.
     */
    @Column(nullable = false, length = 100)
    private String password;

    /**
     * Contador de intentos de inicio de sesión fallidos consecutivos.
     * Se incrementa cada vez que el usuario ingresa una contraseña
     * incorrecta y se reinicia a 0 cada vez que inicia sesión exitosamente.
     * Es la pieza de información clave para implementar la regla de negocio
     * de bloqueo de cuenta tras 3 intentos fallidos.
     */
    @Column(nullable = false)
    private Integer intentosFallidos;

    /**
     * Indica si la cuenta está bloqueada (true) o habilitada (false) para
     * iniciar sesión. Se establece en true automáticamente cuando el
     * contador "intentosFallidos" alcanza el límite de 3 intentos consecutivos.
     */
    @Column(nullable = false)
    private Boolean cuentaBloqueada;

    /**
     * Constructor vacío (sin argumentos).
     * JPA/Hibernate lo REQUIERE obligatoriamente: internamente, cuando
     * Hibernate recupera filas de la base de datos, primero instancia el
     * objeto usando este constructor vacío (mediante reflexión) y luego le
     * asigna los valores de cada columna a través de los setters o del
     * acceso directo a los campos.
     */
    public Persona() {
    }

    /**
     * Constructor de conveniencia para crear una nueva Persona con todos
     * sus datos de una sola vez (usado, por ejemplo, en el servicio de
     * registro). Se inicializan explícitamente los valores por defecto de
     * intentosFallidos (0) y cuentaBloqueada (false), ya que toda cuenta
     * nueva debe nacer sin intentos fallidos y sin estar bloqueada.
     */
    public Persona(String nombre, String apellido, String documento,
                    LocalDate fechaNacimiento, String correoPersonal, String password) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.fechaNacimiento = fechaNacimiento;
        this.correoPersonal = correoPersonal;
        this.password = password;
        this.intentosFallidos = 0;
        this.cuentaBloqueada = false;
    }

    // ==========================================================================
    // GETTERS Y SETTERS
    // ==========================================================================
    // Se escriben de forma explícita (sin usar Lombok) para que el código sea
    // 100% legible y depurable sin necesidad de configurar un procesador de
    // anotaciones adicional en el IDE. Hibernate y Spring (por ejemplo, al
    // enlazar los datos de un formulario con th:field) acceden a estos
    // campos a través de estos getters/setters siguiendo la convención
    // JavaBean (getNombre()/setNombre(), etc.).
    // ==========================================================================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Integer getIntentosFallidos() {
        return intentosFallidos;
    }

    public void setIntentosFallidos(Integer intentosFallidos) {
        this.intentosFallidos = intentosFallidos;
    }

    public Boolean getCuentaBloqueada() {
        return cuentaBloqueada;
    }

    public void setCuentaBloqueada(Boolean cuentaBloqueada) {
        this.cuentaBloqueada = cuentaBloqueada;
    }
}
