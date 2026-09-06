# Spark Admin App — Spring Boot + Spring Security + Thymeleaf

Aplicación web de ejemplo que integra la plantilla visual **Spark Admin**
con un módulo completo de autenticación (registro / login) sobre
**Spring Boot 3**, **Spring MVC**, **Spring Data JPA**, **Spring Security**
y **Thymeleaf**.

## Requisito de negocio implementado

- El **correo personal** (`correoPersonal`) es el nombre de usuario para
  iniciar sesión.
- Si el correo **no existe**, se muestra un mensaje invitando a registrarse.
- Si el correo existe pero la **contraseña es incorrecta**, se cuenta el
  intento. Al llegar a **3 intentos fallidos consecutivos**, la cuenta se
  bloquea (`cuentaBloqueada = true`) y se impide el acceso con el mensaje:
  *"Cuenta bloqueada por superar el límite de 3 intentos fallidos"*.
- Un **login exitoso** antes del tercer fallo reinicia el contador a `0`.

## Cómo ejecutar el proyecto

Requisitos: JDK 17+ y Maven 3.8+ (o el wrapper `mvnw` si lo agrega a su IDE).

```bash
cd spark-admin-app
mvn spring-boot:run
```

La aplicación queda disponible en: <http://localhost:8080>

- `/login` — inicio de sesión
- `/registro` — alta de nuevos usuarios
- `/dashboard` — panel protegido (requiere sesión iniciada)
- `/h2-console` — consola web de la base de datos H2 en memoria
  (JDBC URL: `jdbc:h2:mem:sparkadmindb`, usuario `sa`, sin contraseña)

También puede generar el `.jar` ejecutable:

```bash
mvn clean package
java -jar target/spark-admin-app.jar
```

## Estructura del proyecto

```
spark-admin-app/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/com/sparkadmin/app/
│   │   │   ├── SparkAdminApplication.java        # Clase de arranque (main)
│   │   │   │
│   │   │   ├── config/                           # Configuración transversal
│   │   │   │   ├── SecurityConfig.java                       # SecurityFilterChain, beans (PasswordEncoder, etc.)
│   │   │   │   ├── CustomAuthenticationSuccessHandler.java   # Reinicia intentosFallidos en login exitoso
│   │   │   │   └── CustomAuthenticationFailureHandler.java   # Distingue: no registrado / bloqueado / credenciales
│   │   │   │
│   │   │   ├── controller/                       # Capa web (Spring MVC)
│   │   │   │   ├── AuthController.java           # GET /login , GET /
│   │   │   │   ├── RegistroController.java       # GET/POST /registro
│   │   │   │   └── DashboardController.java      # GET /dashboard (protegido)
│   │   │   │
│   │   │   ├── service/                          # Reglas de negocio
│   │   │   │   ├── PersonaService.java                 # Interfaz del servicio
│   │   │   │   ├── PersonaDetailsService.java          # Puente Persona -> Spring Security (UserDetailsService)
│   │   │   │   └── impl/PersonaServiceImpl.java        # Implementación (registro, conteo de intentos, bloqueo)
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   └── PersonaRepository.java        # Interfaz JpaRepository<Persona, Long>
│   │   │   │
│   │   │   ├── model/
│   │   │   │   └── Persona.java                  # Entidad JPA (Persona / Usuario)
│   │   │   │
│   │   │   └── dto/
│   │   │       └── RegistroDTO.java              # Datos + validaciones del formulario de registro
│   │   │
│   │   └── resources/
│   │       ├── application.properties            # Configuración (H2, JPA, Thymeleaf, logging)
│   │       ├── static/
│   │       │   ├── css/main.css                  # Hoja de estilos de Spark Admin
│   │       │   ├── js/{auth.js, dashboard.js}     # Scripts de interacción de Spark Admin
│   │       │   ├── libs/{bootstrap, bootstrap-icons, apexcharts, flatpickr}
│   │       │   └── images/                       # Logos, avatar, favicon
│   │       └── templates/
│   │           ├── login.html                    # Basada en page-login.html
│   │           ├── registro.html                 # Basada en ui-forms.html
│   │           └── dashboard.html                # Basada en index.html
│   │
│   └── test/java/com/sparkadmin/app/             # (paquete reservado para pruebas)
```

## Notas de diseño

- **Arquitectura en capas estricta**: `controller` nunca accede al
  `repository` directamente; siempre pasa por `service`.
- **BCrypt**: las contraseñas nunca se almacenan ni se comparan en texto
  plano; se usa `PasswordEncoder` (`BCryptPasswordEncoder`) en todo momento.
- **H2 en memoria**: pensada para pruebas locales rápidas sin instalar
  ningún motor de base de datos externo. El `pom.xml` y
  `application.properties` incluyen, comentado, cómo migrar a MySQL.
- Todo el código Java incluye comentarios extensos explicando el rol de
  cada clase, cada anotación de Spring utilizada y el paso a paso de la
  lógica de bloqueo de cuentas.
