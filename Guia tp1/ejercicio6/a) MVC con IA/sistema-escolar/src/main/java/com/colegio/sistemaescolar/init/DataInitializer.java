package com.colegio.sistemaescolar.init;

import com.colegio.sistemaescolar.model.Aula;
import com.colegio.sistemaescolar.model.Grado;
import com.colegio.sistemaescolar.model.Materia;
import com.colegio.sistemaescolar.model.Nivel;
import com.colegio.sistemaescolar.repository.AulaRepository;
import com.colegio.sistemaescolar.repository.GradoRepository;
import com.colegio.sistemaescolar.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga datos iniciales (grados, aulas y materias) la primera vez que arranca la aplicación.
 *
 * <p>{@link CommandLineRunner} se ejecuta una vez, justo después de iniciar el contexto de Spring.
 * Cada catálogo solo se siembra si su tabla está vacía, por lo que es seguro reiniciar la aplicación.
 * Como aún no hay usuario autenticado, la auditoría registra "sistema" como autor.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GradoRepository gradoRepository;
    private final AulaRepository aulaRepository;
    private final MateriaRepository materiaRepository;

    @Override
    public void run(String... args) {
        if (gradoRepository.count() == 0) {
            List<Grado> grados = new ArrayList<>();
            for (int n = 1; n <= 6; n++) {
                grados.add(crearGrado(n + "° de Primaria", Nivel.PRIMARIA));
            }
            for (int n = 1; n <= 5; n++) {
                grados.add(crearGrado(n + "° de Secundaria", Nivel.SECUNDARIA));
            }
            gradoRepository.saveAll(grados);
            log.info("Grados iniciales creados: {}", grados.size());
        }

        if (aulaRepository.count() == 0) {
            List<Aula> aulas = new ArrayList<>();
            for (int n = 101; n <= 106; n++) {
                aulas.add(crearAula("Aula " + n, 30));
            }
            aulas.add(crearAula("Pabellón A", 40));
            aulaRepository.saveAll(aulas);
            log.info("Aulas iniciales creadas: {}", aulas.size());
        }

        if (materiaRepository.count() == 0) {
            materiaRepository.saveAll(List.of(
                    crearMateria("Matemática", "Aritmética, álgebra y geometría"),
                    crearMateria("Lengua y Literatura", "Lectura, escritura y análisis de textos"),
                    crearMateria("Ciencias Naturales", "Biología, física y química básicas"),
                    crearMateria("Ciencias Sociales", "Historia, geografía y formación ciudadana"),
                    crearMateria("Inglés", "Lengua extranjera"),
                    crearMateria("Educación Física", "Actividad física y deporte"),
                    crearMateria("Educación Artística", "Música, plástica y expresión")));
            log.info("Materias iniciales creadas.");
        }
    }

    private Grado crearGrado(String nombre, Nivel nivel) {
        Grado g = new Grado();
        g.setNombre(nombre);
        g.setNivel(nivel);
        return g;
    }

    private Aula crearAula(String codigo, int capacidad) {
        Aula a = new Aula();
        a.setCodigo(codigo);
        a.setCapacidad(capacidad);
        return a;
    }

    private Materia crearMateria(String nombre, String descripcion) {
        Materia m = new Materia();
        m.setNombre(nombre);
        m.setDescripcion(descripcion);
        return m;
    }
}
