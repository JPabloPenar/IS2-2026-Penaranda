# Datos de prueba para la carga

Antes de correr los escenarios, levanta la aplicación con el perfil "carga":

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=carga
```

Con ese perfil, `CargaDataInitializer` (ver `src/main/java/com/clubdeportivo/init/`) crea:
- Un usuario de recepción: `recepcion` / `Recepcion123`
- 500 socios (cada uno con su grupo familiar) con DNIs correlativos `40000001` a `40000500`
- A cada socio se le emite una cuota del mes en curso; la mitad queda **pagada** y la otra mitad
  **pendiente**, para poder medir tanto el camino "acceso permitido" como "acceso rechazado".

Los scripts de k6 generan los DNIs a partir de ese rango (`40000001` + `__VU * __ITER`), así que
no hace falta un archivo CSV aparte para el escenario de accesos.
