package com.example.vitatrack;

public class Habito {
    private String nombre;
    private String progreso;

    // Constructor vacío (requerido por Firestore para la deserialización)
    public Habito() {}

    public Habito(String nombre, String progreso) {
        this.nombre = nombre;
        this.progreso = progreso;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getProgreso() { return progreso; }
    public void setProgreso(String progreso) { this.progreso = progreso; }
}
