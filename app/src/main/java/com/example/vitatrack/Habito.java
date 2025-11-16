package com.example.vitatrack;

public class Habito {
    private String nombre;
    private String progreso;

    public Habito(String nombre, String progreso) {
        this.nombre = nombre;
        this.progreso = progreso;
    }

    public String getNombre() { return nombre; }
    public String getProgreso() { return progreso; }
}