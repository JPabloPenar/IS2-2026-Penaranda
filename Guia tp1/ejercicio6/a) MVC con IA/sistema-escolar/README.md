# Sistema Escolar (Spring Boot 3 + Thymeleaf + MySQL)

Aplicación web MVC para que los docentes gestionen alumnos y calificaciones.

## Requisitos
- JDK 17 o superior
- Maven 3.9+
- MySQL 8.x en ejecución
- Una cuenta de Mailtrap (o Gmail con contraseña de aplicación) para probar el correo de bienvenida

## Configuración
Los valores por defecto están en `src/main/resources/application.properties`. Se pueden sobrescribir con variables de entorno:

| Variable | Por defecto | Uso |
|---|---|---|
| `DB_HOST` / `DB_PORT` / `DB_NAME` | localhost / 3306 / sistema_escolar | Conexión MySQL |
| `DB_USER` / `DB_PASSWORD` | root / root | Credenciales MySQL |
| `MAIL_HOST` / `MAIL_PORT` | sandbox.smtp.mailtrap.io / 2525 | Servidor SMTP |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | (placeholders) | Credenciales SMTP |
| `MAIL_FROM` | no-reply@colegio.local | Remitente |
| `APP_BASE_URL` | http://localhost:8080 | Enlace del correo de bienvenida |

Para Gmail: `MAIL_HOST=smtp.gmail.com`, `MAIL_PORT=587`, usuario = tu cuenta y contraseña = contraseña de aplicación.

## Ejecutar
```bash
mvn spring-boot:run
```
Abrir http://localhost:8080. La primera vez se crean las tablas (Hibernate `ddl-auto=update`) y se cargan grados, aulas y materias de ejemplo (`DataInitializer`). Luego: **Registrarme como docente** → ingresar → cargar alumnos y notas.

## Estructura
```
com.colegio.sistemaescolar
├── config/       SecurityConfig, JpaAuditingConfig, AuditorAwareImpl, AsyncConfig
├── model/        BaseAuditableEntity, Docente, Alumno, Grado, Aula, Materia, Nota + enums
├── dto/          DocenteRegistroDTO, DocenteResponseDTO, CambioPasswordDTO, AlumnoDTO, NotaDTO, ...
├── mapper/       Conversión entidad <-> DTO
├── repository/   Interfaces JpaRepository
├── service/      Interfaces + impl/ (Docente, Email, Alumno, Nota, Catalogo)
├── controller/   Auth, Dashboard, Docente, Alumno, Nota
├── exception/    Excepciones de negocio y manejador global
└── init/         DataInitializer
```

## Reglas de negocio implementadas
- Email de docente único; contraseñas con BCrypt; confirmación de contraseña.
- DNI/matrícula de alumno único.
- Un aula no puede superar su capacidad.
- Una sola nota por alumno + materia + período; escala 0 a 10 (aprobado desde 6, ajustable en `NotaMapper`).
- Auditoría automática (fecha y usuario de creación/modificación) en todas las entidades.

## Notas
- La relación Docente ↔ Materias/Grados/Aulas está modelada (tablas `docente_materia`, `docente_grado`, `docente_aula`) pero aún no hay pantalla para administrarla.
- En producción cambiar `spring.jpa.hibernate.ddl-auto` a `validate` y usar migraciones (Flyway/Liquibase).
