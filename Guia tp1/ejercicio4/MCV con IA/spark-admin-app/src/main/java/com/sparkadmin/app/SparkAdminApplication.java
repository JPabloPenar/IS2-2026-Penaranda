package com.sparkadmin.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * ============================================================================
 * SparkAdminApplication
 * ============================================================================
 * Esta es la clase de arranque ("entry point") de toda la aplicación Spring
 * Boot. Contiene el método main() que es el que la JVM ejecuta al lanzar
 * el .jar (java -jar spark-admin-app.jar) o al correr la app desde el IDE.
 *
 * @SpringBootApplication:
 *   Es una "meta-anotación" que combina, en una sola, tres anotaciones:
 *
 *   1) @Configuration:
 *      Marca esta clase como una fuente de definiciones de beans (aunque en
 *      este proyecto no definimos beans aquí directamente, sino en la clase
 *      SecurityConfig de config/, la anotación habilita ese mecanismo).
 *
 *   2) @EnableAutoConfiguration:
 *      Le dice a Spring Boot: "mira las dependencias (starters) que hay en
 *      el classpath y configura automáticamente lo que haga falta". Por
 *      ejemplo, al detectar spring-boot-starter-web configura un servidor
 *      Tomcat embebido y Spring MVC; al detectar spring-boot-starter-data-jpa
 *      configura un EntityManager y un DataSource; al detectar H2 en el
 *      classpath, configura automáticamente la base de datos en memoria.
 *
 *   3) @ComponentScan:
 *      Le indica a Spring que escanee este paquete (com.sparkadmin.app) y
 *      todos sus subpaquetes (controller, service, repository, config, etc.)
 *      en busca de clases anotadas con @Component, @Service, @Repository,
 *      @Controller, @Configuration, etc., para registrarlas automáticamente
 *      como "beans" dentro del contenedor de Spring (el "ApplicationContext").
 *      Por eso es importante que esta clase esté en el paquete raíz
 *      (com.sparkadmin.app), de forma que el escaneo alcance a todos los
 *      subpaquetes del proyecto.
 * ============================================================================
 */
@SpringBootApplication
public class SparkAdminApplication {

    /**
     * Método main(): punto de entrada estándar de cualquier aplicación Java.
     *
     * SpringApplication.run(...) realiza, entre otras cosas:
     *   - Crea el "ApplicationContext" (el contenedor de Spring que gestiona
     *     el ciclo de vida de todos los beans de la aplicación).
     *   - Ejecuta el proceso de auto-configuración descrito arriba.
     *   - Levanta el servidor web embebido (Tomcat) en el puerto configurado
     *     en application.properties (por defecto 8080).
     *   - Deja la aplicación corriendo, escuchando peticiones HTTP.
     *
     * @param args argumentos de línea de comandos (no se usan en este proyecto)
     */
    public static void main(String[] args) {
        SpringApplication.run(SparkAdminApplication.class, args);
    }
}
