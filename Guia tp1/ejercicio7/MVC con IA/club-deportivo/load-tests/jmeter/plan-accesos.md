# Plan de JMeter "Accesos - hora pico" (equivalente a escenario-accesos.js)

Árbol del Test Plan (para recrearlo en la interfaz de JMeter o generar el .jmx a mano):

```
Test Plan: Club Deportivo - Accesos
└── Stepping Thread Group "Hora pico"
    ├── Start Threads Count: 50, incrementando a 150, 300, 500, 700 (uno por escalon)
    ├── Time entre escalones: 60-120 s (igual que las "stages" del script de k6)
    ├── HTTP Cookie Manager (sostiene JSESSIONID entre requests)
    ├── HTTP Request Defaults: Server = ${BASE_URL}, Port = 8080
    ├── CSV Data Set Config: dnis.csv (una columna "dni" con 40000001..40000500)
    │
    ├── HTTP Request "Login (GET)"        -> GET  /login
    ├── HTTP Request "Login (POST)"       -> POST /login   [username, password]
    ├── Response Assertion                -> código 302/303 (login OK)
    │
    ├── Loop Controller (iteraciones por hilo)
    │   ├── HTTP Request "Buscar por DNI" -> GET  /accesos/buscar?dni=${dni}
    │   ├── Response Assertion            -> contiene ${dni}
    │   ├── Uniform Random Timer          -> 300 ms (tiempo de mirar la foto)
    │   ├── HTTP Request "Registrar acceso" -> POST /accesos/registrar [dni, tipoAcceso]
    │   ├── Response Assertion            -> código 302/303
    │   └── Uniform Random Timer          -> 1000 ms (ritmo entre personas)
    │
    ├── Aggregate Report          (RPS, p95/p99, % error, por request)
    └── Response Codes per Second (tasa de error a lo largo del tiempo)
```

Umbrales sugeridos (iguales a los `thresholds` de k6): p95 de "Registrar acceso" < 500 ms,
p95 general < 800 ms, tasa de error HTTP < 1%.
