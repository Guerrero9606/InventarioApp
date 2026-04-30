package com.example.inventarioapp;

public class Articulo {
    private int codigo;
    private String descripcion;
    private double precio;

    public Articulo(int codigo, String descripcion, double precio){
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.precio = precio;
    }

    public int getCodigo() { return codigo; }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }
}
