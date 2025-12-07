package com.example.vitatrack;

public class Habito {
    private String nombre;
    private String descripcion; // Cambié "progreso" por "descripcion"

    // Constructor vacío (requerido por Firestore para la deserialización)
    public Habito() {}

    // Constructor modificado
    public Habito(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() { // Metodo getter modificado
        return descripcion;
    }

    public void setDescripcion(String descripcion) { // Metodo setter modificado
        this.descripcion = descripcion;
    }
}
