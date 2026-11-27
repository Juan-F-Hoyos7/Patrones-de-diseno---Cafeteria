package com.cafeteria.patterns.decorator;

import com.cafeteria.model.Producto;

public class AdicionDecorator extends ProductoDecorator {
    private final String nombreAdicion;
    private final double costoAdicion;

    public AdicionDecorator(Producto productoEnvuelto, String nombreAdicion, double costoAdicion) {
        super(productoEnvuelto);
        if (nombreAdicion == null || nombreAdicion.isBlank()) {
            throw new IllegalArgumentException("El nombre de la adición no puede estar vacío.");
        }
        if (costoAdicion < 0) {
            throw new IllegalArgumentException("El costo de la adición no puede ser negativo.");
        }
        this.nombreAdicion = nombreAdicion.trim();
        this.costoAdicion = costoAdicion;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + " + nombreAdicion;
    }

    @Override
    public double getPrecio() {
        return super.getPrecio() + costoAdicion;
    }
}