package org.example.logica;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.List;

@Entity
public class Carrera implements Serializable {
       
    @Id
    @GeneratedValue(strategy= GenerationType.SEQUENCE)
    private int id;
    private String nombre;
    
    @OneToMany (mappedBy="carre")
    private List<Materia> listaMateria;
    
    
    public Carrera() {
    }

    public Carrera(String nombre, List<Materia> listaMateria) {
        this.nombre = nombre;
        this.listaMateria = listaMateria;
    }

    public List<Materia> getListaMateria() {
        return listaMateria;
    }

    public void setListaMateria(List<Materia> listaMateria) {
        this.listaMateria = listaMateria;
    }

    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    
}
