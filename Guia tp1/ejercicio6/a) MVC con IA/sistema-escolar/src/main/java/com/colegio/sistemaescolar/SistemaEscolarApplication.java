package com.colegio.sistemaescolar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación.
 *
 * <p>{@code @SpringBootApplication} combina tres anotaciones:
 * <ul>
 *   <li>{@code @Configuration}: esta clase puede declarar beans.</li>
 *   <li>{@code @EnableAutoConfiguration}: Spring Boot configura automáticamente
 *       JPA, Security, Thymeleaf, Mail, etc. según las dependencias del pom.xml.</li>
 *   <li>{@code @ComponentScan}: escanea este paquete y sus subpaquetes buscando
 *       {@code @Controller}, {@code @Service}, {@code @Component}, {@code @Repository}...</li>
 * </ul>
 */
@SpringBootApplication
public class SistemaEscolarApplication {

    public static void main(String[] args) {
        SpringApplication.run(SistemaEscolarApplication.class, args);
    }
}
