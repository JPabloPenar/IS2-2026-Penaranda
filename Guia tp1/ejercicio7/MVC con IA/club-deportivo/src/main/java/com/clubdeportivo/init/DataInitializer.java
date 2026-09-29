package com.clubdeportivo.init;

import com.clubdeportivo.model.Rol;
import com.clubdeportivo.model.Usuario;
import com.clubdeportivo.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Crea el usuario ADMINISTRADOR inicial la primera vez que arranca la aplicación (solo si la tabla
 * de usuarios está vacía), para que siempre haya alguien que pueda entrar y dar de alta al resto
 * del personal desde /admin/usuarios.
 *
 * <p>{@code @Profile("!carga")}: NO se ejecuta con el perfil "carga" (ver {@code CargaDataInitializer},
 * que crea su propio usuario de prueba).
 */
@Slf4j
@Component
@Profile("!carga")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${club.admin.username}")
    private String adminUsername;

    @Value("${club.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setNombre("Administrador");
            admin.setApellido("del Club");
            admin.setEmail(adminUsername + "@club.local");
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRol(Rol.ROLE_ADMIN);
            usuarioRepository.save(admin);
            log.warn("Se creó el usuario administrador inicial '{}'. Cambia su contraseña cuanto antes.", adminUsername);
        }
    }
}
