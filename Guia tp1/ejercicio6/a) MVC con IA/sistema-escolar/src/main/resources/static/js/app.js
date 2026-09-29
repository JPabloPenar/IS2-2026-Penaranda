/* app.js - Comportamientos mínimos de la interfaz (sin dependencias). */

document.addEventListener('DOMContentLoaded', function () {
    // Cierra automáticamente los avisos de éxito después de 6 segundos.
    document.querySelectorAll('.alert-success').forEach(function (aviso) {
        setTimeout(function () {
            if (window.bootstrap) { bootstrap.Alert.getOrCreateInstance(aviso).close(); }
        }, 6000);
    });

    // Botones "mostrar/ocultar contraseña": <button data-ver-password="idDelInput">
    document.querySelectorAll('[data-ver-password]').forEach(function (boton) {
        boton.addEventListener('click', function () {
            var campo = document.getElementById(boton.getAttribute('data-ver-password'));
            if (!campo) { return; }
            var oculto = campo.type === 'password';
            campo.type = oculto ? 'text' : 'password';
            boton.setAttribute('aria-label', oculto ? 'Ocultar contraseña' : 'Mostrar contraseña');
            boton.querySelector('.bi').className = oculto ? 'bi bi-eye-slash' : 'bi bi-eye';
        });
    });
});
