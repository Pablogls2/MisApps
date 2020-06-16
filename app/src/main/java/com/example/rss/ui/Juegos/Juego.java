package com.example.rss.ui.Juegos;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Juego implements Serializable {

    private String titulo;
    private String plataforma;
    private String fecha;
    private Float precio;
    private Bitmap imagen;
    private int id;

    public Juego() {
    }

    public Juego(String titulo, String platamforma, String  fecha, Float precio, Bitmap imagen) {
        this.titulo = titulo;
        this.plataforma = platamforma;
        this.fecha = fecha;
        this.precio = precio;
        this.imagen = imagen;
    }

    public Juego(String titulo, String plataforma, String fecha, Float precio, Bitmap imagen, int id) {
        this.titulo = titulo;
        this.plataforma = plataforma;
        this.fecha = fecha;
        this.precio = precio;
        this.imagen = imagen;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getPlatamforma() {
        return plataforma;
    }

    public void setPlatamforma(String platamforma) {
        this.plataforma = platamforma;
    }

    public String  getFecha() {
        return fecha;
    }

    public void setFecha(String  fecha) {
        this.fecha = fecha;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Bitmap getImagen() {
        return imagen;
    }

    public void setImagen(Bitmap imagen) {
        this.imagen = imagen;
    }
}
