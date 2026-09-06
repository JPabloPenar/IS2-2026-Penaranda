package com.sparkadmin.app.config;

import com.sparkadmin.app.service.PersonaDetailsService;
import com.sparkadmin.app.service.PersonaService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 * ============================================================================
 * SecurityConfig
 * ============================================================================
 * Clase central de configuración de Spring Security para toda la
 * aplicación. Aquí se define:
 *   - Qué rutas son públicas y cuáles requieren autenticación.
 *   - Cómo se realiza el login (formLogin: formulario propio, no el genérico
 *     que trae Spring Security por defecto).
 *   - Qué ocurre exactamente cuando el login falla o tiene éxito (delegando
 *     en los manejadores personalizados creados en este mismo paquete).
 *   - Cómo se codifican las contraseñas (BCrypt).
 *   - Cómo se conecta Spring Security con nuestra propia fuente de usuarios
 *     (PersonaDetailsService) en lugar de usar usuarios en memoria.
 *
 * @Configuration:
 *   Indica que esta clase contiene definiciones de beans (métodos anotados
 *   con @Bean) que Spring debe registrar en el contenedor de la aplicación
 *   durante el arranque.
 *
 * @EnableWebSecurity:
 *   Activa el soporte de seguridad web de Spring Security e integra la
 *   configuración personalizada definida en esta clase con la cadena de
 *   filtros ("filter chain") que intercepta TODAS las peticiones HTTP
 *   entrantes antes de que lleguen a los controladores.
 * ============================================================================
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Se inyecta el servicio de negocio de Persona porque los manejadores
     * personalizados de éxito/fallo de autenticación (creados más abajo
     * como beans) lo necesitan para aplicar las reglas de conteo de
     * intentos fallidos y reinicio del contador.
     */
    private final PersonaService personaService;

    /**
     * Servicio que le indica a Spring Security CÓMO cargar los datos de un
     * usuario (Persona) a partir de su correo, para poder compararlos
     * contra lo ingresado en el formulario de login.
     */
    private final PersonaDetailsService personaDetailsService;

    public SecurityConfig(PersonaService personaService, PersonaDetailsService personaDetailsService) {
        this.personaService = personaService;
        this.personaDetailsService = personaDetailsService;
    }

    /**
     * -----------------------------------------------------------------
     * BEAN: PasswordEncoder
     * -----------------------------------------------------------------
     * @Bean:
     *   Indica que el objeto devuelto por este método debe ser registrado
     *   y gestionado por el contenedor de Spring, de forma que pueda ser
     *   inyectado en cualquier otra clase que lo necesite (en este
     *   proyecto: PersonaServiceImpl, para encriptar contraseñas al
     *   registrar usuarios, y el propio Spring Security internamente, para
     *   comparar contraseñas durante el login).
     *
     * BCryptPasswordEncoder:
     *   Implementación del algoritmo BCrypt, un algoritmo de hashing de
     *   contraseñas diseñado específicamente para ser LENTO a propósito
     *   (a diferencia de MD5/SHA256, que son rápidos y por eso inadecuados
     *   para contraseñas). Esa lentitud deliberada hace mucho más costoso
     *   para un atacante intentar "adivinar" contraseñas por fuerza bruta.
     *   Además, BCrypt incorpora automáticamente un "salt" aleatorio en
     *   cada hash generado.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * -----------------------------------------------------------------
     * BEAN: DaoAuthenticationProvider
     * -----------------------------------------------------------------
     * Es el componente interno de Spring Security responsable de:
     *   1) Invocar a nuestro PersonaDetailsService para obtener el
     *      UserDetails correspondiente al correo ingresado.
     *   2) Comparar la contraseña ingresada (texto plano) contra el hash
     *      almacenado, usando el PasswordEncoder configurado.
     *   3) Verificar el estado de la cuenta (bloqueada, deshabilitada,
     *      expirada, etc.) ANTES de aceptar la autenticación.
     *
     * setHideUserNotFoundExceptions(false):
     *   Este es un punto CRÍTICO para poder cumplir el requisito de
     *   distinguir "usuario no registrado" de "contraseña incorrecta".
     *   Por defecto (true), Spring Security oculta la excepción real
     *   UsernameNotFoundException y la reemplaza silenciosamente por una
     *   BadCredentialsException genérica, como medida de seguridad
     *   estándar (para no revelarle a un atacante si un correo existe o
     *   no en el sistema mediante mensajes de error diferentes). En este
     *   proyecto, el propio enunciado exige explícitamente mostrar un
     *   mensaje distinto invitando al registro, por lo que se desactiva
     *   conscientemente esa ocultación.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(personaDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * -----------------------------------------------------------------
     * BEAN: AuthenticationSuccessHandler
     * -----------------------------------------------------------------
     * Se registra como bean nuestra implementación personalizada (ver
     * CustomAuthenticationSuccessHandler) que reinicia el contador de
     * intentos fallidos tras un login exitoso.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new CustomAuthenticationSuccessHandler(personaService);
    }

    /**
     * -----------------------------------------------------------------
     * BEAN: AuthenticationFailureHandler
     * -----------------------------------------------------------------
     * Se registra como bean nuestra implementación personalizada (ver
     * CustomAuthenticationFailureHandler) que distingue entre los tres
     * escenarios de fallo de login (usuario no registrado, credenciales
     * incorrectas, cuenta bloqueada).
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new CustomAuthenticationFailureHandler(personaService);
    }

    /**
     * -----------------------------------------------------------------
     * BEAN: SecurityFilterChain
     * -----------------------------------------------------------------
     * Este es el bean MÁS importante de toda la configuración: define la
     * cadena de filtros de seguridad que se aplica a cada petición HTTP
     * entrante. Se construye usando el patrón "builder" sobre el objeto
     * HttpSecurity que Spring inyecta automáticamente como parámetro.
     *
     * @param http objeto de configuración fluida de seguridad HTTP,
     *             inyectado automáticamente por Spring
     * @return la cadena de filtros de seguridad ya construida y lista para
     *         ser registrada como bean
     * @throws Exception las distintas operaciones de configuración de
     *                    HttpSecurity pueden lanzar excepciones checked
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // -------------------------------------------------------
                // AUTORIZACIÓN DE RUTAS (authorizeHttpRequests)
                // -------------------------------------------------------
                // Define, petición por petición, qué rutas son públicas y
                // cuáles requieren que el usuario esté autenticado.
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas: cualquiera puede acceder sin
                        // haber iniciado sesión.
                        .requestMatchers(
                                "/login",          // formulario de inicio de sesión
                                "/registro",        // formulario de registro (GET y POST)
                                "/css/**",           // hojas de estilo de Spark Admin
                                "/js/**",            // scripts de Spark Admin
                                "/libs/**",           // librerías de terceros de Spark Admin
                                "/images/**",          // imágenes de Spark Admin
                                "/h2-console/**"        // consola web de la base de datos H2 (solo desarrollo)
                        ).permitAll()
                        // Cualquier otra ruta no listada arriba (por
                        // ejemplo, "/dashboard") exige que el usuario esté
                        // autenticado; si no lo está, Spring Security lo
                        // redirige automáticamente a la página de login.
                        .anyRequest().authenticated()
                )

                // -------------------------------------------------------
                // CONFIGURACIÓN DEL FORMULARIO DE LOGIN (formLogin)
                // -------------------------------------------------------
                .formLogin(form -> form
                        // URL de la página que muestra el formulario de
                        // login (nuestra propia vista, no la genérica de
                        // Spring Security).
                        .loginPage("/login")
                        // URL a la que el formulario HTML envía sus datos
                        // (method="post"). Spring Security intercepta
                        // automáticamente este POST; nunca llega a un
                        // @Controller nuestro.
                        .loginProcessingUrl("/login")
                        // Nombre del campo del formulario que contiene el
                        // identificador de usuario. En nuestro caso el
                        // formulario envía el correo personal en un campo
                        // llamado "username" (ver login.html).
                        .usernameParameter("username")
                        .passwordParameter("password")
                        // Se delega el resultado del login en nuestros
                        // manejadores personalizados en lugar de usar el
                        // comportamiento por defecto.
                        .successHandler(authenticationSuccessHandler())
                        .failureHandler(authenticationFailureHandler())
                        // Toda persona (autenticada o no) debe poder ver
                        // la página de login para poder loguearse.
                        .permitAll()
                )

                // -------------------------------------------------------
                // CONFIGURACIÓN DE LOGOUT
                // -------------------------------------------------------
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                // -------------------------------------------------------
                // PROVEEDOR DE AUTENTICACIÓN PERSONALIZADO
                // -------------------------------------------------------
                // Se le indica explícitamente a Spring Security que use
                // nuestro DaoAuthenticationProvider (configurado arriba
                // con PersonaDetailsService y BCrypt) en lugar del
                // proveedor por defecto.
                .authenticationProvider(authenticationProvider())

                // -------------------------------------------------------
                // AJUSTE ESPECÍFICO PARA LA CONSOLA DE H2
                // -------------------------------------------------------
                // La consola de H2 se muestra dentro de un <frame> HTML.
                // Por defecto, Spring Security bloquea que cualquier
                // página de la aplicación se muestre dentro de un frame
                // (protección contra ataques de tipo "clickjacking"). Esta
                // línea relaja esa protección ÚNICAMENTE para permitir que
                // la consola de H2 (pensada solo para desarrollo local)
                // funcione correctamente.
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                // -------------------------------------------------------
                // CSRF (Cross-Site Request Forgery)
                // -------------------------------------------------------
                // Se desactiva la protección CSRF ÚNICAMENTE para las
                // peticiones dirigidas a la consola de H2, ya que esa
                // consola realiza peticiones internas que no son
                // compatibles con el mecanismo de token CSRF de Spring
                // Security. El resto de la aplicación (login, registro)
                // mantiene la protección CSRF activa por defecto: por eso
                // los formularios de login.html y registro.html deben
                // incluir el campo oculto con el token CSRF, que Thymeleaf
                // agrega automáticamente en los formularios que usan
                // th:action gracias a la integración con Spring Security.
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                );

        return http.build();
    }
}
