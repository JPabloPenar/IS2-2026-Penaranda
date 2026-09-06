package com.sparkadmin.app.service;

import com.sparkadmin.app.dto.RegistroDTO;
import com.sparkadmin.app.model.Persona;

import java.util.Optional;

/**
 * ============================================================================
 * PersonaService (Interfaz de la capa de servicio)
 * ============================================================================
 * ¿Por qué una interfaz además de la clase de implementación
 * (PersonaServiceImpl)?
 *
 *   1) Principio de "programar contra interfaces, no contra
 *      implementaciones": los controladores dependerán de esta interfaz
 *      (PersonaService), no de la clase concreta. Esto permite, por
 *      ejemplo, reemplazar la implementación real por una implementación
 *      "falsa" (mock) durante las pruebas unitarias sin tocar el código
 *      del controlador.
 *
 *   2) Documentación del "contrato": esta interfaz describe QUÉ hace el
 *      servicio (las reglas de negocio disponibles), mientras que la clase
 *      de implementación describe CÓMO lo hace.
 *
 * La capa de servicio es donde vive toda la LÓGICA DE NEGOCIO del sistema:
 * el registro de nuevos usuarios, la validación de reglas de bloqueo de
 * cuenta, el conteo de intentos fallidos, etc. Los controladores NUNCA
 * deben acceder directamente al repositorio para aplicar reglas de negocio;
 * siempre lo hacen a través del servicio, manteniendo así una arquitectura
 * en capas limpia y ordenada.
 * ============================================================================
 */
public interface PersonaService {

    /**
     * Registra una nueva Persona/Usuario en el sistema a partir de los datos
     * capturados en el formulario de registro (RegistroDTO).
     *
     * Responsabilidades esperadas de la implementación:
     *   - Verificar que el correo y el documento no estén ya registrados.
     *   - Encriptar la contraseña en texto plano recibida (BCrypt) antes de
     *     construir la entidad Persona.
     *   - Inicializar intentosFallidos=0 y cuentaBloqueada=false.
     *   - Persistir la nueva Persona a través del repositorio.
     *
     * @param dto datos capturados y ya validados (@Valid) del formulario de registro
     * @return la entidad Persona recién creada y persistida (con su id generado)
     */
    Persona registrarNuevaPersona(RegistroDTO dto);

    /**
     * Busca una Persona por su correo personal (que actúa como "username").
     *
     * @param correoPersonal correo ingresado en el formulario de login
     * @return un Optional con la Persona si existe, o Optional.empty() si
     *         no hay ninguna cuenta registrada con ese correo.
     */
    Optional<Persona> buscarPorCorreo(String correoPersonal);

    /**
     * Registra un intento de inicio de sesión FALLIDO (contraseña
     * incorrecta) para el usuario identificado por su correo.
     *
     * Lógica de negocio que debe aplicar la implementación:
     *   1) Incrementar en 1 el contador intentosFallidos de la Persona.
     *   2) Si, tras el incremento, intentosFallidos alcanza el valor 3,
     *      marcar cuentaBloqueada = true (bloqueo automático de la cuenta).
     *   3) Persistir los cambios en la base de datos.
     *
     * @param correoPersonal correo del usuario que falló al autenticarse
     * @return la entidad Persona ya actualizada, para que quien invoque este
     *         método pueda decidir qué mensaje mostrar (por ejemplo,
     *         verificar si quedó bloqueada con este intento)
     */
    Persona registrarIntentoFallido(String correoPersonal);

    /**
     * Reinicia el contador de intentos fallidos a 0 tras un inicio de
     * sesión EXITOSO, tal como exige el enunciado ("si el login es exitoso
     * antes del tercer fallo, el contador debe reiniciarse a 0").
     *
     * @param correoPersonal correo del usuario que acaba de autenticarse correctamente
     */
    void reiniciarIntentosFallidos(String correoPersonal);

    /**
     * Verifica si ya existe una cuenta registrada con el correo indicado.
     * Se utiliza durante el proceso de registro para evitar duplicados y
     * mostrar un mensaje de validación claro al usuario.
     *
     * @param correoPersonal correo a verificar
     * @return true si ya existe una Persona con ese correo, false en caso contrario
     */
    boolean existeCorreo(String correoPersonal);

    /**
     * Verifica si ya existe una cuenta registrada con el documento indicado.
     *
     * @param documento número de documento a verificar
     * @return true si ya existe una Persona con ese documento, false en caso contrario
     */
    boolean existeDocumento(String documento);
}
