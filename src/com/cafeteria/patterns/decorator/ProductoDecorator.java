package com.cafeteria.patterns.decorator;

import com.cafeteria.model.Producto;

public abstract class ProductoDecorator implements Producto {
    protected final Producto productoEnvuelto;

    protected ProductoDecorator(Producto productoEnvuelto) {
        if (productoEnvuelto == null) {
            throw new IllegalArgumentException("El producto a envolver no puede ser nulo.");
        }
        this.productoEnvuelto = productoEnvuelto;
    }

    @Override
    public String getDescripcion() {
        return productoEnvuelto.getDescripcion();
    }

    @Override
    public double getPrecio() {
        return productoEnvuelto.getPrecio();
    }
}