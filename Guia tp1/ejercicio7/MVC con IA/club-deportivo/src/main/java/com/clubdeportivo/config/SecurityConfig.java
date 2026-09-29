package com.clubdeportivo.config;

import com.clubdeportivo.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion de Spring Security.
 *
 * <p>Control de acceso en DOS niveles:
 * <ol>
 *   <li><b>Por URL</b> ({@link #securityFilterChain}): que rutas son publicas, cuales exigen sesion
 *       y cuales exigen rol ADMIN.</li>
 *   <li><b>Por metodo</b> ({@code @PreAuthorize}, habilitado con {@code @EnableMethodSecurity}):
 *       reglas mas finas en los controladores, por ejemplo "solo el ADMIN ve el reporte de pagos".</li>
 * </ol>
 * Roles: {@code ROLE_ADMIN} (todo) y {@code ROLE_RECEPCION} (porteria, socios y cobros).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Publicas: login, recursos estaticos, pagina de error y verificacion de salud
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/error", "/actuator/health").permitAll()
                // Administracion de empleados: solo ADMIN (hasRole agrega el prefijo ROLE_ solo)
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Todo lo demas (incluidas las fotos de rostro en /fotos/**) exige sesion iniciada
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/accesos", true)   // La porteria es la pantalla principal
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")                  // POST con token CSRF
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );
        return http.build();
    }

    /** BCrypt: hash con sal aleatoria y costo configurable; nunca se guardan claves en texto plano. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Carga al empleado por username y lo traduce a un {@link User} de Spring Security. */
    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository usuarioRepository) {
        return username -> usuarioRepository.findByUsernameIgnoreCase(username.trim())
                .map(u -> User.withUsername(u.getUsername())
                        .password(u.getPassword())
                        .authorities(u.getRol().name())      // ROLE_ADMIN o ROLE_RECEPCION
                        .disabled(!u.isActivo())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario inexistente"));
    }
}
