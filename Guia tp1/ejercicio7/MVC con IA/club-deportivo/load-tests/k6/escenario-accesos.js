/**
 * Escenario 1: "Hora pico de entrada" — 500 socios intentando ingresar de forma simultánea.
 *
 * Simula la apertura del club en un horario concurrido: cada usuario virtual (VU) representa
 * un socio distinto que llega a la portería, el sistema lo busca por DNI (paso 1: GET /accesos/buscar,
 * lo que también ejercita la consulta "¿el grupo tiene deuda?") y confirma el ingreso
 * (paso 2: POST /accesos/registrar).
 *
 * Ejecutar:
 *   k6 run --env BASE_URL=http://localhost:8080 --env USUARIO=recepcion --env PASSWORD=Recepcion123 escenario-accesos.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const USUARIO = __ENV.USUARIO || 'recepcion';
const PASSWORD = __ENV.PASSWORD || 'Recepcion123';
const TOTAL_SOCIOS = 500; // debe coincidir con club.carga.total-socios

// Métricas propias, además de las que k6 calcula solas (http_req_duration, http_req_failed...)
const tasaError = new Rate('tasa_error_dominio');
const duracionBusqueda = new Trend('duracion_busqueda_dni');
const duracionRegistro = new Trend('duracion_registro_acceso');

/**
 * Parámetros de la prueba de STRESS: rampa de carga hasta encontrar el punto de saturación.
 * Se sube de a escalones y se observa cuándo el p95 se dispara o el error rate supera el 1%.
 * Para una prueba de CARGA simple (no de stress), reemplazar "stages" por:
 *   { vus: 100, duration: '5m' }
 */
export const options = {
    scenarios: {
        rampa_hasta_saturacion: {
            executor: 'ramping-vus',
            startVUs: 0,
            stages: [
                { duration: '1m', target: 50 },   // calentamiento
                { duration: '2m', target: 150 },  // carga normal estimada (horario pico habitual)
                { duration: '2m', target: 300 },  // carga alta (varios molinetes a la vez)
                { duration: '2m', target: 500 },  // los 500 socios del escenario, simultáneos
                { duration: '2m', target: 700 },  // sobrecarga: para encontrar el punto de quiebre
                { duration: '1m', target: 0 },    // enfriamiento
            ],
        },
    },
    thresholds: {
        // Umbrales de aceptación: si se incumplen, k6 termina con código de salida distinto de 0
        // (útil para un pipeline de CI que quiera fallar el build ante una regresión de performance).
        http_req_duration: ['p(95)<800'],   // p95 de TODAS las requests bajo 800 ms
        'http_req_duration{name:registrar_acceso}': ['p(95)<500'],
        tasa_error_dominio: ['rate<0.01'],  // menos del 1% de resultados inesperados
        http_req_failed: ['rate<0.01'],     // menos del 1% de errores HTTP (5xx, timeouts)
    },
};

/** Login por formulario: guarda la cookie de sesión que usan el resto de las requests del VU. */
function iniciarSesion() {
    const csrfPage = http.get(`${BASE_URL}/login`);
    const res = http.post(`${BASE_URL}/login`, { username: USUARIO, password: PASSWORD }, {
        redirects: 0,
    });
    check(res, { 'login redirige (sesión iniciada)': (r) => r.status === 302 || r.status === 303 });
}

export function setup() {
    // Se valida una vez, al arrancar la prueba, que el servidor responde antes de generar carga real.
    const res = http.get(`${BASE_URL}/login`);
    check(res, { 'servidor disponible': (r) => r.status === 200 });
}

export default function () {
    iniciarSesion();

    // Cada iteración de cada VU "es" un socio distinto: se reparte el rango de 500 DNIs de prueba.
    const indice = (__VU * 997 + __ITER) % TOTAL_SOCIOS; // dispersión simple para no pegarle siempre al mismo
    const dni = String(40000001 + indice);

    // Paso 1: buscar por DNI (dispara la consulta de "cuotas adeudadas" del grupo)
    const resBusqueda = http.get(`${BASE_URL}/accesos/buscar?dni=${dni}`, {
        tags: { name: 'buscar_por_dni' },
    });
    duracionBusqueda.add(resBusqueda.timings.duration);
    const encontrado = resBusqueda.status === 200 && resBusqueda.body.includes(dni);
    tasaError.add(!encontrado);
    check(resBusqueda, { 'encontró a la persona': () => encontrado });

    sleep(0.3); // tiempo que un empleado tarda en mirar la foto antes de confirmar

    // Paso 2: confirmar la entrada (alterna entrada/salida como en un dia real, sin logica de estado)
    const tipoAcceso = __ITER % 2 === 0 ? 'ENTRADA' : 'SALIDA';
    const resRegistro = http.post(`${BASE_URL}/accesos/registrar`, { dni, tipoAcceso }, {
        redirects: 0,
        tags: { name: 'registrar_acceso' },
    });
    duracionRegistro.add(resRegistro.timings.duration);
    check(resRegistro, { 'acceso procesado (redirect)': (r) => r.status === 302 || r.status === 303 });

    sleep(1); // ritmo aproximado entre una persona y la siguiente en el molinete
}
