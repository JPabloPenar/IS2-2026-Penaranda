/* app.js - Comportamientos minimos de la interfaz (sin dependencias externas). */

document.addEventListener('DOMContentLoaded', function () {
    // Cierra los avisos de exito automaticamente a los 6 segundos.
    document.querySelectorAll('.alert-success').forEach(function (aviso) {
        setTimeout(function () {
            if (window.bootstrap) { bootstrap.Alert.getOrCreateInstance(aviso).close(); }
        }, 6000);
    });

    // Previsualizacion de la foto antes de subirla: <input data-preview="idDestino">
    document.querySelectorAll('[data-preview]').forEach(function (input) {
        input.addEventListener('change', function () {
            var destino = document.getElementById(input.getAttribute('data-preview'));
            if (!destino || !input.files || !input.files[0]) { return; }
            var lector = new FileReader();
            lector.onload = function (evento) {
                destino.src = evento.target.result;
                destino.classList.remove('d-none');
                var marcador = document.getElementById(destino.id + '-placeholder');
                if (marcador) { marcador.classList.add('d-none'); }
            };
            lector.readAsDataURL(input.files[0]);
        });
    });
});
