document.getElementById('myForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Evita que el formulario se envíe automáticamente
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    
    if (username && email) { // Verifica que ambos campos tengan contenido
        console.log('Usuario:', username);
        console.log('Email:', email);
        console.log('Formulario enviado correctamente.');
    } else {
        console.log('Por favor, complete todos los campos del formulario.');
        return;
    }

    if (username.length < 5) { // Verifica que el nombre de usuario tenga al menos 5 caracteres
        console.log('El nombre de usuario debe tener al menos 5 caracteres.');
        return;
    }
    if (!email.includes('@')) { 
        console.log('El correo electrónico debe contener un "@" válido.');
        return;
    }
});