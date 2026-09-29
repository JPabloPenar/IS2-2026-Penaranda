package com.colegio.sistemaescolar.service.impl;

import com.colegio.sistemaescolar.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

/**
 * Envía correos HTML usando {@link JavaMailSender}.
 *
 * <p>{@code @Service}: marca la clase como componente de la capa de servicio.
 * {@code @RequiredArgsConstructor} (Lombok) genera el constructor con los campos {@code final},
 * y Spring inyecta las dependencias por constructor (la forma recomendada).
 * {@code @Slf4j} crea un logger llamado {@code log}.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    /** Motor de Thymeleaf reutilizado para generar el HTML del correo desde una plantilla. */
    private final SpringTemplateEngine templateEngine;

    /** Remitente configurado en application.properties (app.mail.from). */
    @Value("${app.mail.from}")
    private String remitente;

    /** URL base de la aplicación, para el enlace de ingreso del correo. */
    @Value("${app.base-url}")
    private String urlBase;

    /**
     * {@code @Async}: se ejecuta en otro hilo; quien llama no espera al servidor SMTP.
     * Como corre fuera del hilo de la petición, cualquier error se registra en el log
     * y NO se propaga: un fallo de correo no debe impedir que el registro se complete.
     */
    @Async
    @Override
    public void enviarBienvenida(String destinatario, String nombre) {
        try {
            // 1) Se prepara el contexto con las variables que usa la plantilla email/bienvenida.html
            Context contexto = new Context();
            contexto.setVariable("nombre", nombre);
            contexto.setVariable("urlLogin", urlBase + "/login");

            // 2) Thymeleaf convierte la plantilla en un String HTML
            String contenidoHtml = templateEngine.process("email/bienvenida", contexto);

            // 3) Se arma el mensaje MIME (UTF-8 para tildes y ñ)
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");
            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject("Te damos la bienvenida al Sistema Escolar");
            helper.setText(contenidoHtml, true); // true = el contenido es HTML

            // 4) Envío
            mailSender.send(mensaje);
            log.info("Correo de bienvenida enviado a {}", destinatario);
        } catch (MessagingException | RuntimeException e) {
            log.error("No se pudo enviar el correo de bienvenida a {}: {}", destinatario, e.getMessage());
        }
    }
}
