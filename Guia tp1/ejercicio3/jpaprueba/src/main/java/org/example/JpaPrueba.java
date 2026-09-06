package org.example;

import java.util.Date;
import java.util.LinkedList;
import org.example.logica.Alumno;
import org.example.logica.Carrera;
import org.example.logica.Controladora;
import org.example.logica.Materia;


public class JpaPrueba {
    public static void main(String[] args) {
        
        Controladora control = new Controladora();
        
        LinkedList<Materia> listaMaterias = new LinkedList<>();
        Carrera carre = new Carrera("Ciencias de la computación", listaMaterias);
        control.crearCarrera(carre);
                
        Materia mate1 = new Materia("Programación I", "Semestral", carre);
        Materia mate2 = new Materia("Programación II", "Semestral", carre);
        Materia mate3 = new Materia("Matemática", "Anual", carre);
        
        control.crearMateria(mate1);
        control.crearMateria(mate2);
        control.crearMateria(mate3);
        
        
        listaMaterias.add(mate1);
        listaMaterias.add(mate2);
        listaMaterias.add(mate3);
        
        carre.setListaMateria(listaMaterias);
        control.editarCarrera(carre);
        
        //Alumno alu = new Alumno("Juan", "Dominguez", new Date(), carre);
        
    }
}
