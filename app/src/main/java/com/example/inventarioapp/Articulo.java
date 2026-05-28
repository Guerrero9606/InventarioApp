package com.example.inventarioapp;

public class Articulo {
    private int codigo;
    private String descripcion;
    private double precio;
    private boolean oferta;

    public Articulo(){

    }
    public Articulo(int codigo, String descripcion, double precio, boolean oferta){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.oferta = oferta;
    }

    public int getCodigo() { return codigo; }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

     boolean isOferta() {
        return oferta;
    }
}
