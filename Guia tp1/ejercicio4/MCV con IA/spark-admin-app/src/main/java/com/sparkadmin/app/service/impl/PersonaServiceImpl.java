package com.sparkadmin.app.service.impl;

import com.sparkadmin.app.dto.RegistroDTO;
import com.sparkadmin.app.model.Persona;
import com.sparkadmin.app.repository.PersonaRepository;
import com.sparkadmin.app.service.PersonaService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ============================================================================
 * PersonaServiceImpl (Implementación de PersonaService)
 * ============================================================================
 * Aquí vive el "cómo" de la lógica de negocio declarada en la interfaz
 * PersonaService. Esta clase es el corazón de las reglas descritas en el
 * enunciado: registro de usuarios, conteo de intentos fallidos y bloqueo
 * automático de cuentas.
 *
 * @Service:
 *   Especialización de @Component que marca esta clase como un "bean" de
 *   la capa de SERVICIO (lógica de negocio). Al llevar esta anotación,
 *   Spring la detecta durante el @ComponentScan y la registra en el
 *   contenedor, permitiendo que sea "inyectada" (mediante @Autowired /
 *   inyección por constructor) en cualquier otra clase que la necesite
 *   (por ejemplo, los controladores o el UserDetailsService de seguridad).
 *   Semánticamente, distingue esta clase de un @Repository o un
 *   @Controller, aunque técnicamente las tres son "beans" de Spring.
 * ============================================================================
 */
@Service
public class PersonaServiceImpl implements PersonaService {

    /**
     * Dependencia hacia la capa de acceso a datos. Se declara "final" y se
     * inyecta por CONSTRUCTOR (ver más abajo), que es la forma recomendada
     * de inyección de dependencias en Spring moderno, porque:
     *   - Permite que el campo sea inmutable (final).
     *   - Hace explícitas y obligatorias las dependencias de la clase.
     *   - Facilita enormemente las pruebas unitarias (se puede instanciar
     *     PersonaServiceImpl pasando un mock de PersonaRepository sin
     *     necesidad de un contenedor de Spring).
     */
    private final PersonaRepository personaRepository;

    /**
     * Codificador de contraseñas (BCrypt), definido como @Bean en
     * config/SecurityConfig. Se utiliza para transformar la contraseña en
     * texto plano que llega desde el formulario de registro en un HASH
     * irreversible antes de guardarla en la base de datos.
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Valor de negocio: cantidad máxima de intentos fallidos consecutivos
     * permitidos antes de bloquear la cuenta. Se define como constante para
     * que, si el requerimiento cambiara en el futuro (por ejemplo, a 5
     * intentos), solo haya que modificar este único valor.
     */
    private static final int MAXIMO_INTENTOS_FALLIDOS = 3;

    /**
     * Constructor utilizado por Spring para la inyección de dependencias.
     * Al haber un único constructor, Spring lo detecta automáticamente y
     * no es necesario anotarlo explícitamente con @Autowired (aunque en
     * versiones antiguas de Spring sí era obligatorio).
     */
    public PersonaServiceImpl(PersonaRepository personaRepository, PasswordEncoder passwordEncoder) {
        this.personaRepository = personaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * {@inheritDoc}
     *
     * @Transactional:
     *   Envuelve la ejecución de este método en una transacción de base de
     *   datos administrada por Spring. Esto garantiza atomicidad: si algo
     *   falla a mitad de camino (por ejemplo, una excepción inesperada),
     *   Spring revierte (rollback) cualquier cambio parcial ya realizado,
     *   evitando dejar la base de datos en un estado inconsistente (por
     *   ejemplo, evitar una Persona a medio guardar).
     */
    @Override
    @Transactional
    public Persona registrarNuevaPersona(RegistroDTO dto) {
        // Paso 1: encriptar la contraseña en texto plano recibida del
        // formulario usando BCrypt. BCrypt genera automáticamente un "salt"
        // aleatorio distinto en cada llamada, por lo que dos usuarios con
        // la misma contraseña tendrán hashes almacenados completamente
        // diferentes, dificultando ataques de diccionario/rainbow tables.
        String passwordEncriptada = passwordEncoder.encode(dto.getPassword());

        // Paso 2: construir la entidad Persona a partir de los datos del
        // DTO, usando el constructor de conveniencia que además inicializa
        // intentosFallidos=0 y cuentaBloqueada=false para toda cuenta nueva.
        Persona nuevaPersona = new Persona(
                dto.getNombre(),
                dto.getApellido(),
                dto.getDocumento(),
                dto.getFechaNacimiento(),
                dto.getCorreoPersonal(),
                passwordEncriptada
        );

        // Paso 3: persistir la entidad. personaRepository.save(...) genera
        // un INSERT (ya que el id todavía es null) y devuelve la misma
        // entidad ya con el id autogenerado por la base de datos.
        return personaRepository.save(nuevaPersona);
    }

    /**
     * {@inheritDoc}
     * Método de solo lectura: no modifica datos, simplemente delega en el
     * repositorio la búsqueda por correo.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<Persona> buscarPorCorreo(String correoPersonal) {
        return personaRepository.findByCorreoPersonal(correoPersonal);
    }

    /**
     * {@inheritDoc}
     *
     * Esta es la implementación central de la regla de negocio de bloqueo
     * de cuentas descrita en el enunciado. Paso a paso:
     *
     *   1) Se busca la Persona por su correo. Si en este punto no existiera
     *      (situación anómala, ya que este método solo debería invocarse
     *      cuando SÍ sabemos que el usuario existe, tras descartar
     *      previamente el caso "usuario no registrado" en el
     *      AuthenticationFailureHandler), se lanza una excepción, porque
     *      no tiene sentido "contar intentos" de alguien que no existe.
     *
     *   2) Se incrementa en 1 el contador intentosFallidos actual.
     *
     *   3) Se compara el nuevo valor contra el máximo permitido
     *      (MAXIMO_INTENTOS_FALLIDOS = 3). Si se alcanzó o superó ese
     *      límite, se marca cuentaBloqueada = true. A partir de ese
     *      momento, el propio Spring Security impedirá el acceso (ver
     *      PersonaDetailsService, donde el UserDetails se construye con
     *      accountLocked = cuentaBloqueada) mostrando el mensaje
     *      "Cuenta bloqueada por superar el límite de 3 intentos fallidos".
     *
     *   4) Se guarda la entidad actualizada en la base de datos.
     */
    @Override
    @Transactional
    public Persona registrarIntentoFallido(String correoPersonal) {
        Persona persona = personaRepository.findByCorreoPersonal(correoPersonal)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No se puede registrar un intento fallido para un correo no registrado: " + correoPersonal));

        // Paso 2: incrementar el contador de intentos fallidos consecutivos.
        int intentosActuales = persona.getIntentosFallidos() == null ? 0 : persona.getIntentosFallidos();
        int nuevosIntentos = intentosActuales + 1;
        persona.setIntentosFallidos(nuevosIntentos);

        // Paso 3: si se alcanzó el límite máximo de intentos, bloquear la cuenta.
        if (nuevosIntentos >= MAXIMO_INTENTOS_FALLIDOS) {
            persona.setCuentaBloqueada(true);
        }

        // Paso 4: persistir los cambios. Como "persona" es una entidad
        // gestionada (fue obtenida en esta misma transacción a través del
        // repositorio), Hibernate detecta los cambios en sus campos y
        // genera automáticamente el UPDATE correspondiente al llamar a
        // save(...) (o incluso al finalizar la transacción, gracias al
        // mecanismo de "dirty checking").
        return personaRepository.save(persona);
    }

    /**
     * {@inheritDoc}
     * Se invoca desde el manejador de éxito de autenticación
     * (CustomAuthenticationSuccessHandler) cada vez que un usuario logra
     * iniciar sesión correctamente, cumpliendo la regla: "si el login es
     * exitoso antes del tercer fallo, el contador debe reiniciarse a 0".
     */
    @Override
    @Transactional
    public void reiniciarIntentosFallidos(String correoPersonal) {
        personaRepository.findByCorreoPersonal(correoPersonal).ifPresent(persona -> {
            persona.setIntentosFallidos(0);
            // No es necesario tocar cuentaBloqueada aquí: si el login fue
            // exitoso es porque la cuenta NO estaba bloqueada (Spring
            // Security ya lo habría impedido antes de llegar a este punto).
            personaRepository.save(persona);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreo(String correoPersonal) {
        return personaRepository.existsByCorreoPersonal(correoPersonal);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeDocumento(String documento) {
        return personaRepository.existsByDocumento(documento);
    }
}
