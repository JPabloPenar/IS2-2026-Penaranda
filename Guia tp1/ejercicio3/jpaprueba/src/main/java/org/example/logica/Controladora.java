package org.example.logica;

import java.util.ArrayList;
import org.example.persistencia.ControladoraPersistencia;

public class Controladora {

    ControladoraPersistencia controlPersis= new ControladoraPersistencia();
    
    public void crearAlumno(Alumno alu){
        controlPersis.crearAlumno(alu);
    }
   
    public void eliminarAlumno(int id){
        controlPersis.eliminarAlumno(id);
    }
    public void editarAlumno(Alumno alu){
        controlPersis.editarAlumno(alu);
    }
    
    public Alumno traerAlumno(int id){
        return controlPersis.traerAlumno(id);
    }

    public ArrayList<Alumno> traerListaAlumnos(){
        return controlPersis.traerListaAlumnos();
    }
    //CARRERA---------------------------
    public void crearCarrera(Carrera carre){
        controlPersis.crearCarrera(carre);
    }
   
    public void eliminarCarrera(int id){
        controlPersis.eliminarCarrera(id);
    }
    public void editarCarrera(Carrera carre){
        controlPersis.editarCarrera(carre);
    }
    
    public Carrera traerCarrera(int id){
        return controlPersis.traerCarrera(id);
    }

    public ArrayList<Carrera> traerListaCarreras(){
        return controlPersis.traerListaCarrera();
    }
    //MATERIA---------------------------
    
    public void crearMateria(Materia mate){
        controlPersis.crearMateria(mate);
    }
   
    public void eliminarMateria(int id){
        controlPersis.eliminarMateria(id);
    }
    public void editarMateria(Materia mate){
        controlPersis.editarMateria(mate);
    }
    
    public Materia traerMateria(int id){
        return controlPersis.traerMateria(id);
    }

    public ArrayList<Materia> traerListaMaterias(){
        return controlPersis.traerListaMateria();
    }
}
