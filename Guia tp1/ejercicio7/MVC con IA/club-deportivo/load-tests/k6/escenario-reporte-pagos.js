/**
 * Escenario 2: concurrencia en el reporte de pagos — varios administradores consultando la
 * recaudación del mes al mismo tiempo (por ejemplo, el día de cierre de caja), mientras
 * la portería sigue registrando accesos de fondo.
 *
 * Ejecutar:
 *   k6 run --env BASE_URL=http://localhost:8080 --env ADMIN_USER=admin --env ADMIN_PASSWORD=Admin1234 escenario-reporte-pagos.js
 */
import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ADMIN_USER = __ENV.ADMIN_USER || 'admin';
const ADMIN_PASSWORD = __ENV.ADMIN_PASSWORD || 'Admin1234';

export const options = {
    scenarios: {
        // Carga CONSTANTE (no rampa): representa una ventana corta de mucha consulta simultánea,
        // como el cierre de caja de fin de mes, más que un crecimiento gradual.
        cierre_de_caja: {
            executor: 'constant-vus',
            vus: 30,
            duration: '3m',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<1500'], // el reporte agrega datos: se tolera mas que un pasaje de molinete
        http_req_failed: ['rate<0.01'],
    },
};

function iniciarSesionAdmin() {
    http.get(`${BASE_URL}/login`);
    const res = http.post(`${BASE_URL}/login`, { username: ADMIN_USER, password: ADMIN_PASSWORD }, { redirects: 0 });
    check(res, { 'login admin OK': (r) => r.status === 302 || r.status === 303 });
}

export default function () {
    iniciarSesionAdmin();

    const hoy = new Date().toISOString().slice(0, 10);
    const primerDiaMes = hoy.slice(0, 8) + '01';

    const res = http.get(`${BASE_URL}/pagos/reporte?desde=${primerDiaMes}&hasta=${hoy}`, {
        tags: { name: 'reporte_pagos' },
    });
    check(res, { 'reporte devuelto': (r) => r.status === 200 });

    sleep(2);
}
