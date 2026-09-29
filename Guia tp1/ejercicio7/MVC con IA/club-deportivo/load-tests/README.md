# Pruebas de carga y stress

Se eligió **k6** como herramienta principal porque los escenarios se versionan como código
(JavaScript), corren desde la terminal o un pipeline de CI, y sus métricas (RPS, p95, tasa de error)
se resumen solas al final de la corrida. Al final de este documento se explica cómo trasladar los
mismos escenarios a **Apache JMeter** o **Gatling**, tal como pide el enunciado.

## Requisitos
- [k6](https://k6.io/docs/get-started/installation/) instalado (`brew install k6`, `choco install k6`, o el binario oficial).
- La aplicación corriendo con el perfil `carga` (ver `k6/setup-datos.md`) para tener 500 socios de prueba.

## Escenarios incluidos

| Script | Qué mide | Corresponde a |
|---|---|---|
| `k6/escenario-accesos.js` | Búsqueda por DNI + registro de entrada/salida, con rampa de 0 a 700 usuarios virtuales | "500 socios intentando ingresar simultáneamente en horario pico" |
| `k6/escenario-reporte-pagos.js` | 30 administradores consultando el reporte de recaudación en simultáneo durante 3 minutos | "Concurrencia en el reporte de pagos" |

## Ejecutar

```bash
cd load-tests/k6
k6 run --env BASE_URL=http://localhost:8080 escenario-accesos.js
k6 run --env BASE_URL=http://localhost:8080 escenario-reporte-pagos.js
```

## Métricas a monitorear

- **RPS (requests por segundo):** `http_reqs` en el resumen de k6. Interesa el RPS sostenido en
  cada escalón de la rampa, no solo el pico.
- **Latencia p95:** `http_req_duration{p(95)}`, y en particular la métrica etiquetada
  `http_req_duration{name:registrar_acceso}` (el registro de acceso es el camino más sensible,
  porque bloquea el molinete).
- **Tasa de error:** `http_req_failed` (errores HTTP) y `tasa_error_dominio` (respuestas 200 pero
  con un resultado de negocio inesperado, p. ej. "no se encontró a la persona").
- **Del lado del servidor** (con `management.endpoints.web.exposure.include=health,metrics` ya
  habilitado en `application.properties`):
  - `hikaricp.connections.pending` y `hikaricp.connections.active` (`/actuator/metrics/hikaricp.connections.pending`):
    si crece durante la rampa, el pool de conexiones a MySQL es el cuello de botella.
  - `tomcat.threads.busy` / `tomcat.threads.current`: si se satura, subir `server.tomcat.threads.max`
    (ver `application.properties`) o escalar horizontalmente.
  - `jvm.memory.used`: para descartar presión de memoria/GC como causa de la degradación.

## Parámetros de rampa y cómo leer el resultado

El escenario de accesos sube de a escalones (50 → 150 → 300 → 500 → 700 usuarios virtuales) en
lugar de saltar directo a 500, para poder ubicar el **punto de saturación**: el escalón donde el
p95 empieza a crecer de forma no lineal o `http_req_failed` supera el 1%. Ese escalón es la
capacidad real del sistema con la configuración actual (tamaño de pool, hilos de Tomcat, recursos
de la base de datos); el escalón siguiente (700) confirma que efectivamente se degrada más allá de
ese punto y no fue una casualidad puntual.

Si el punto de saturación aparece antes de los 500 usuarios esperados en horario pico, los
candidatos a ajustar son, en este orden: `spring.datasource.hikari.maximum-pool-size`,
`server.tomcat.threads.max`, y por último escalar la instancia (más CPU/RAM) o el propio MySQL.

## Migrar a Apache JMeter

Cada escenario de k6 se traduce a un **Thread Group** de JMeter:

1. **Thread Group** "Accesos - hora pico": usar un **Stepping Thread Group** (plugin oficial de
   JMeter) con los mismos escalones (50, 150, 300, 500, 700 hilos) y las mismas duraciones.
2. Un **HTTP Cookie Manager** por thread group, para sostener la sesión de Spring Security.
3. Una petición **HTTP Request** POST a `/login` con `username`/`password`, seguida de un
   **CSRF Extractor** (o directamente deshabilitar CSRF para pruebas, NUNCA en producción) si el
   formulario de login llegara a exigir token.
4. Dos **HTTP Request** por iteración, igual que en el script: GET `/accesos/buscar?dni=${dni}` y
   POST `/accesos/registrar` con `dni` y `tipoAcceso`, usando una variable JMeter (`${__Random}`
   o un **CSV Data Set Config** con los 500 DNIs) en lugar de la fórmula de `__VU`/`__ITER` de k6.
5. **Listeners:** "Aggregate Report" (para RPS y p95) y "Response Codes per Second" para la tasa
   de error; guardar el `.jtl` para comparar corridas.

El archivo `jmeter/plan-accesos.md` en esta carpeta detalla el árbol completo del plan
(equivalente textual al `.jmx`, para no depender de poder abrir JMeter en este entorno).

## Migrar a Gatling

La estructura es casi 1 a 1: un `Simulation` en Scala con un `httpProtocol` apuntando a `BASE_URL`,
un `scenario` con `exec(http("login").post("/login")...)` seguido de un `repeat`/`during` con las
dos requests, y `setUp(...).protocols(httpProtocol)` con `incrementUsersPerSec` reproduciendo la
misma rampa de escalones.
