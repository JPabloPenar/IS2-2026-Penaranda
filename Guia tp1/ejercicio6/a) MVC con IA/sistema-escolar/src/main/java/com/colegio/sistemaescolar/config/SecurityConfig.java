package com.colegio.sistemaescolar.config;

import com.colegio.sistemaescolar.repository.DocenteRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de Spring Security.
 *
 * <ul>
 *   <li>Rutas públicas: /login, /registro, /css/**, /js/** (más /images/** y /error).</li>
 *   <li>Rutas protegidas: cualquier otra (/dashboard, /docentes/**, /alumnos/**, /notas/**...).</li>
 *   <li>Contraseñas cifradas con BCrypt.</li>
 *   <li>Protección CSRF activa por defecto: los formularios Thymeleaf con {@code th:action}
 *       incluyen el token automáticamente.</li>
 * </ul>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Define la cadena de filtros de seguridad HTTP: qué rutas son públicas,
     * cómo es el login y cómo es el logout.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Recursos y páginas accesibles sin iniciar sesión
                .requestMatchers("/login", "/registro", "/css/**", "/js/**", "/images/**", "/error").permitAll()
                // Todo lo demás exige estar autenticado
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                 // Vista propia (AuthController)
                .loginProcessingUrl("/login")        // URL que recibe el POST del formulario
                .usernameParameter("email")          // El "username" del formulario se llama "email"
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                // Debe invocarse por POST (con token CSRF)
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    /**
     * Codificador de contraseñas. BCrypt aplica sal aleatoria y es deliberadamente lento,
     * lo que dificulta los ataques de fuerza bruta. Nunca se guardan contraseñas en texto plano.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Le dice a Spring Security cómo cargar un usuario a partir del email escrito en el login.
     * Se convierte la entidad {@code Docente} en un {@link User} de Spring Security
     * (esta clase interna no se expone a las vistas; es solo para autenticación).
     */
    @Bean
    public UserDetailsService userDetailsService(DocenteRepository docenteRepository) {
        return username -> docenteRepository.findByEmail(username.trim().toLowerCase())
                .map(docente -> User.withUsername(docente.getEmail())
                        .password(docente.getPassword())      // Hash BCrypt almacenado
                        .authorities(docente.getRol())         // Ej.: ROLE_DOCENTE
                        .disabled(!docente.isActivo())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("No existe un docente con ese email"));
    }
}
