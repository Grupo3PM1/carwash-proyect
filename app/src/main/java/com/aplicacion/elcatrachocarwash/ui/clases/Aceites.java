package com.aplicacion.elcatrachocarwash.ui.clases;

public class Aceites {

    private Integer id;
    private String nombre;

    public Aceites() {}

    public Aceites(Integer id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
