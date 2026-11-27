package com.cafeteria.model;

public class ProductoBase implements Producto {
    private final String nombre;
    private final double precio;

    public ProductoBase(String nombre, double precio) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio base no puede ser negativo.");
        }
        this.nombre = nombre.trim();
        this.precio = precio;
    }

    @Override
    public String getDescripcion() {
        return nombre;
    }

    @Override
    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return getDescripcion() + " ($" + String.format("%.2f", getPrecio()) + ")";
    }
}