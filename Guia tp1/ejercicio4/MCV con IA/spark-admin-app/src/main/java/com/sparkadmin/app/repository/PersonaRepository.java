package com.sparkadmin.app.repository;

import com.sparkadmin.app.model.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ============================================================================
 * PersonaRepository
 * ============================================================================
 * Capa de acceso a datos (DAO - Data Access Object), implementada con
 * Spring Data JPA.
 *
 * ¿Qué hace "extends JpaRepository<Persona, Long>"?
 *   JpaRepository es una interfaz de Spring Data que YA trae implementados,
 *   de forma automática (Spring genera una implementación "proxy" en tiempo
 *   de ejecución, sin que el desarrollador escriba una sola línea de SQL),
 *   los métodos CRUD más comunes:
 *     - save(Persona p)          -> INSERT o UPDATE según corresponda
 *     - findById(Long id)        -> SELECT ... WHERE id = ?
 *     - findAll()                -> SELECT * FROM personas
 *     - deleteById(Long id)      -> DELETE ... WHERE id = ?
 *     - count(), existsById(), etc.
 *
 *   Los dos parámetros genéricos indican:
 *     <Persona, Long> -> Persona es la entidad que gestiona este repositorio,
 *                        y Long es el tipo de dato de su clave primaria (id).
 *
 * @Repository:
 *   Anotación de Spring que marca esta interfaz como un "bean" de la capa
 *   de persistencia. Aunque para las interfaces que extienden JpaRepository
 *   Spring Boot es capaz de detectarlas automáticamente sin esta anotación
 *   explícita, se agrega igualmente por claridad y para dejar documentado
 *   el rol de la interfaz dentro de la arquitectura en capas, además de
 *   habilitar la traducción automática de excepciones de la capa de
 *   persistencia (por ejemplo, excepciones específicas de Hibernate) hacia
 *   excepciones estándar de Spring (DataAccessException).
 * ============================================================================
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /**
     * Método de consulta derivado ("query method").
     *
     * Spring Data JPA es capaz de generar automáticamente la consulta SQL
     * correspondiente simplemente analizando el NOMBRE del método, sin que
     * el desarrollador escriba ninguna sentencia SQL/JPQL:
     *
     *   findBy + CorreoPersonal  ->  SELECT p FROM Persona p WHERE p.correoPersonal = :correoPersonal
     *
     * Se devuelve un Optional<Persona> (en lugar de Persona directamente)
     * para representar explícitamente que el resultado PUEDE no existir
     * (correo no registrado), evitando así el riesgo de NullPointerException
     * y obligando a quien llama a este método a manejar ambos casos de forma
     * explícita (usando isPresent(), orElseThrow(), etc.).
     *
     * Este método es la base de:
     *   - El proceso de autenticación (buscar el usuario por su correo).
     *   - La validación de "correo ya registrado" durante el alta de una
     *     nueva Persona.
     */
    Optional<Persona> findByCorreoPersonal(String correoPersonal);

    /**
     * Método derivado que verifica de forma eficiente si ya existe una
     * Persona con un documento determinado, sin necesidad de traer la
     * entidad completa a memoria (genera una consulta tipo
     * "SELECT COUNT(*) > 0 ..." o equivalente, según el dialecto).
     * Se usa durante el registro para no permitir documentos duplicados.
     */
    boolean existsByDocumento(String documento);

    /**
     * Método derivado análogo al anterior, pero para el correo personal.
     * Se usa durante el registro para mostrar un mensaje de validación
     * amigable ("Ya existe una cuenta con ese correo") en lugar de dejar
     * que la restricción "unique" de la base de datos lance una excepción
     * de bajo nivel (ConstraintViolationException) hasta la vista.
     */
    boolean existsByCorreoPersonal(String correoPersonal);
}
