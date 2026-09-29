package com.clubdeportivo.repository;

import com.clubdeportivo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Acceso a datos de {@link Usuario}. {@code @Repository} marca la capa de persistencia y traduce las
 * excepciones de JDBC/Hibernate a excepciones de Spring. Los metodos "derivados" los implementa
 * Spring Data a partir de su nombre.
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}
