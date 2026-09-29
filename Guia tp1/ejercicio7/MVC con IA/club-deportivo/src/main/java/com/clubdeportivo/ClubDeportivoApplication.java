package com.clubdeportivo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del sistema de gestion del Club Deportivo.
 *
 * <p>ARQUITECTURA (MVC en capas):
 * <pre>
 *  Navegador -> Controller (MVC) -> Service (logica de negocio) -> Repository (JPA) -> MySQL
 *                   |                     |
 *                 Vista Thymeleaf        DTOs (nunca entidades hacia la vista)
 * </pre>
 *
 * <p>{@code @SpringBootApplication} = {@code @Configuration} + {@code @EnableAutoConfiguration}
 * + {@code @ComponentScan}: Spring configura JPA, Security, Thymeleaf, etc. segun las dependencias
 * y escanea este paquete buscando {@code @Controller}, {@code @Service}, {@code @Component}...
 */
@SpringBootApplication
public class ClubDeportivoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClubDeportivoApplication.class, args);
    }
}
