# Club Deportivo (Spring Boot 3 + Thymeleaf + MySQL)

Sistema de gestión de un club: socios, grupos familiares, control de acceso por molinete,
cuotas y pagos, con fotos de rostro para validar visualmente el ingreso.

## Requisitos
- JDK 17+, Maven 3.9+, MySQL 8.x
- (Opcional, para las pruebas de carga) [k6](https://k6.io/docs/get-started/installation/)

## Configuración
Valores por defecto en `src/main/resources/application.properties`, sobrescribibles por variable de entorno:

| Variable | Por defecto | Uso |
|---|---|---|
| `DB_HOST`/`DB_PORT`/`DB_NAME` | localhost/3306/club_deportivo | Conexión MySQL |
| `DB_USER`/`DB_PASSWORD` | root/root | Credenciales MySQL |
| `DB_POOL_SIZE` | 30 | Tamaño máximo del pool HikariCP |
| `CLUB_FOTOS_DIR` | ./uploads/fotos | Carpeta local de fotos de rostro |
| `CLUB_ADMIN_USER`/`CLUB_ADMIN_PASSWORD` | admin/Admin1234 | Administrador creado al primer arranque |
| `TOMCAT_MAX_THREADS`/`TOMCAT_ACCEPT_COUNT` | 200/100 | Ajustables al buscar el punto de saturación |

## Ejecutar
```bash
mvn spring-boot:run
```
Abrir http://localhost:8080 → redirige a `/login`. Ingresar con el administrador inicial
(`admin` / `Admin1234` por defecto — **cambiar esta contraseña cuanto antes**, o crear un
usuario nuevo desde **Empleados** y desactivar el admin de arranque).

## Estructura
```
com.clubdeportivo
├── config/       SecurityConfig, JpaAuditingConfig, AuditorAwareImpl, AppConfig
├── model/        BaseAuditableEntity, Persona (Socio/Familiar), GrupoFamiliar, Cuota, Pago,
│                 RegistroAcceso, Usuario + enums
├── dto/          DTOs de entrada y salida de cada flujo
├── mapper/       Conversión entidad <-> DTO
├── repository/   Interfaces JpaRepository (con bloqueo pesimista, proyecciones, JOIN FETCH)
├── service/      Interfaces + impl/ (Socio, Acceso, Pago, Cuota, Usuario, Catalogo,
│                 FileStorage) + pago/ (Strategy: Efectivo, Transferencia, MercadoPago)
├── controller/   Auth, Acceso, Socio, Cuota, Pago, Usuario, Foto
├── exception/    Excepciones de negocio y manejador global
├── tarea/        Tarea programada de vencimiento de cuotas
└── init/         DataInitializer (admin inicial) y CargaDataInitializer (perfil "carga")
```

## Reglas de negocio implementadas
- El DNI es único entre TODAS las personas (socios y familiares).
- Una persona solo **ingresa** si el grupo familiar al que pertenece no tiene cuotas adeudadas
  (vencidas o pendientes ya vencidas); la **salida** siempre se permite. Los intentos rechazados
  también quedan auditados.
- No se puede emitir dos veces la cuota de un mismo grupo para el mismo mes/año.
- Un pago no puede superar el saldo pendiente de la cuota; al completarse el monto total, la
  cuota pasa a PAGADA. El cobro usa **bloqueo pesimista** sobre la cuota para que dos cobros
  simultáneos no la sobrepaguen.
- Cada medio de pago valida sus propios requisitos (patrón *Strategy*, ver `service/pago/`):
  efectivo no exige nada, transferencia y Mercado Pago exigen un comprobante único.
- Las fotos se validan por tipo MIME **y** por contenido real (se intenta decodificar como imagen),
  con límite de tamaño; se guardan con un nombre aleatorio (evita colisiones y *path traversal*).
- El reporte de pagos y la administración de empleados exigen rol `ADMIN`
  (`@PreAuthorize` + reglas de URL en `SecurityConfig`).
- Auditoría automática (fecha y usuario de creación/modificación) en todas las entidades.

## Testing
```bash
mvn test
```
- `PagoServiceTest`: los 3 medios de pago (validaciones propias de cada uno), pago completo vs.
  parcial, rechazo de sobre-pago y de cuota ya pagada.
- `AccesoServiceTest`: entrada permitida/rechazada según la cuota, salida siempre permitida,
  persona dada de baja, DNI inexistente, y sugerencia de entrada/salida según el último pasaje.

Ambos son pruebas **unitarias puras** (JUnit 5 + Mockito, sin levantar Spring ni una base de
datos real), por lo que corren en segundos.

## Pruebas de carga y stress
Ver `load-tests/README.md`: escenarios de k6 (con la guía para trasladarlos a JMeter o Gatling),
parámetros de rampa, umbrales y qué métricas del lado del servidor observar para encontrar el
punto de saturación.

## Notas y alcance
- Las fotos se guardan en el sistema de archivos local (`FileStorageService`); para pasar a un
  almacenamiento en la nube basta con escribir otra implementación de esa interfaz.
- La integración real con la API de Mercado Pago (verificar el pago contra su servidor) queda
  marcada como `TODO` en `PagoMercadoPagoProcesador`; aquí se valida el formato y la unicidad
  del ID de pago, pero no se llama a una API externa real.
- En producción, cambiar `spring.jpa.hibernate.ddl-auto` a `validate` y usar Flyway/Liquibase.
