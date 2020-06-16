package com.example.rss.ui.mapas;
//clase java para guardar
public class Punto {

    //atributos
    private Double longitud;
    private Double latitud;

    //constructor
    public Punto(){

    }

    public Punto(Double latitud, Double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    //getters y setters
    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }
}
