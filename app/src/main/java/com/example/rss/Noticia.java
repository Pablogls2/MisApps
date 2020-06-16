package com.example.rss;

import java.io.Serializable;

public class Noticia implements Serializable {
    private String titular;
    private String descripcion;
    private String enlace;
    private String imagen;
    private String fecha;
    private String autor;

    public Noticia() {
    }

    public Noticia(String titular, String descripcion, String enlace, String imagen, String fecha,String autor) {
        this.titular = titular;
        this.descripcion = descripcion;
        this.enlace = enlace;
        this.imagen = imagen;
        this.fecha = fecha;
        this.autor=autor;
    }

    public String getTitular() {
        return this.titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEnlace() {
        return enlace;
    }

    public void setEnlace(String enlace) {
        this.enlace = enlace;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }
}
