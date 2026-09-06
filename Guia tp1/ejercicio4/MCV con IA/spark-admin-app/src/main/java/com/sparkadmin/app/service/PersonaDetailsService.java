package com.sparkadmin.app.service;

import com.sparkadmin.app.model.Persona;
import com.sparkadmin.app.repository.PersonaRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * ============================================================================
 * PersonaDetailsService
 * ============================================================================
 * Esta clase es el "puente" entre el modelo de dominio propio de la
 * aplicación (la entidad Persona) y el modelo que Spring Security entiende
 * internamente para autenticar usuarios (la interfaz UserDetails).
 *
 * Spring Security NO sabe nada sobre "Persona", "correoPersonal" ni
 * "cuentaBloqueada": solo sabe hablar en términos de UserDetailsService y
 * UserDetails. Esta clase traduce de un mundo al otro.
 *
 * implements UserDetailsService:
 *   Es la interfaz estándar de Spring Security con un único método,
 *   loadUserByUsername(String username). Cuando un usuario envía el
 *   formulario de login, el AuthenticationManager de Spring Security
 *   invoca automáticamente este método pasándole el valor del campo
 *   "username" del formulario (en nuestro caso, el correoPersonal) para
 *   averiguar quién es y cuál es su contraseña encriptada almacenada.
 *
 * @Service:
 *   Marca esta clase como un bean gestionado por Spring para que pueda ser
 *   inyectado automáticamente en la configuración de seguridad
 *   (SecurityConfig) y en el propio AuthenticationManager interno de
 *   Spring Security.
 * ============================================================================
 */
@Service
public class PersonaDetailsService implements UserDetailsService {

    private final PersonaRepository personaRepository;

    public PersonaDetailsService(PersonaRepository personaRepository) {
        this.personaRepository = personaRepository;
    }

    /**
     * Método invocado automáticamente por Spring Security durante el
     * proceso de autenticación (cada vez que alguien intenta iniciar
     * sesión).
     *
     * @param username en este proyecto, el valor de este parámetro es en
     *                  realidad el "correoPersonal" ingresado en el
     *                  formulario de login (ver SecurityConfig, donde se
     *                  configura usernameParameter para apuntar al campo
     *                  correcto del formulario).
     *
     * @return un objeto UserDetails "de sistema" (org.springframework.
     *         security.core.userdetails.User) construido a partir de los
     *         datos reales de la Persona encontrada.
     *
     * @throws UsernameNotFoundException si NO existe ninguna Persona
     *         registrada con ese correo. Es MUY IMPORTANTE que esta
     *         excepción se propague "visible" (no oculta como
     *         BadCredentialsException genérica) para que el
     *         AuthenticationFailureHandler personalizado pueda distinguir
     *         este caso particular ("usuario no registrado") del caso de
     *         "contraseña incorrecta". Esa distinción se logra
     *         configurando explícitamente el DaoAuthenticationProvider con
     *         setHideUserNotFoundExceptions(false) en SecurityConfig.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Paso 1: buscar la Persona en la base de datos por su correo.
        Persona persona = personaRepository.findByCorreoPersonal(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe ningún usuario registrado con el correo: " + username));

        // Paso 2: traducir la entidad Persona al objeto UserDetails que
        // Spring Security necesita para comparar contraseñas y verificar
        // el estado de la cuenta.
        //
        // User.builder() es una clase de utilidad de Spring Security para
        // construir fácilmente un UserDetails "estándar":
        //
        //   .username(...)          -> identificador de login (el correo)
        //   .password(...)          -> el HASH BCrypt ya almacenado (jamás
        //                              se envía ni se compara en texto
        //                              plano; Spring Security se encarga de
        //                              usar el PasswordEncoder configurado
        //                              para comparar el hash almacenado
        //                              contra el hash de la contraseña
        //                              ingresada en el formulario).
        //   .authorities(...)       -> roles/permisos del usuario. Como el
        //                              enunciado no pide roles diferenciados,
        //                              se asigna un único rol genérico
        //                              "ROLE_USER" a todas las cuentas.
        //   .accountLocked(...)     -> ¡CLAVE para la regla de bloqueo!
        //                              Se enlaza directamente al campo
        //                              cuentaBloqueada de la entidad. Si es
        //                              true, Spring Security lanzará
        //                              automáticamente una LockedException
        //                              ANTES de siquiera comparar la
        //                              contraseña, impidiendo el acceso.
        return User.builder()
                .username(persona.getCorreoPersonal())
                .password(persona.getPassword())
                .authorities("ROLE_USER")
                .accountLocked(Boolean.TRUE.equals(persona.getCuentaBloqueada()))
                .disabled(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .build();
    }
}
