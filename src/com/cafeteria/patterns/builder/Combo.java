package com.cafeteria.patterns.builder;

import com.cafeteria.model.Producto;

import java.util.ArrayList;
import java.util.List;

public class Combo implements Producto {
    private final String nombre;
    private final Producto principal;
    private final Producto acompanamiento;
    private final Producto bebida;
    private final Producto postre;
    private final double descuentoCombo;

    private Combo(ComboBuilder builder) {
        this.nombre = builder.nombre;
        this.principal = builder.principal;
        this.acompanamiento = builder.acompanamiento;
        this.bebida = builder.bebida;
        this.postre = builder.postre;
        this.descuentoCombo = builder.descuentoCombo;
    }

    @Override
    public String getDescripcion() {
        List<String> items = new ArrayList<>();
        if (principal != null) items.add(principal.getDescripcion());
        if (acompanamiento != null) items.add(acompanamiento.getDescripcion());
        if (bebida != null) items.add(bebida.getDescripcion());
        if (postre != null) items.add(postre.getDescripcion());
        return nombre + " [" + String.join(", ", items) + "]";
    }

    @Override
    public double getPrecio() {
        double suma = 0.0;
        if (principal != null) suma += principal.getPrecio();
        if (acompanamiento != null) suma += acompanamiento.getPrecio();
        if (bebida != null) suma += bebida.getPrecio();
        if (postre != null) suma += postre.getPrecio();
        double total = suma - descuentoCombo;
        return Math.max(0.0, total);
    }

    public static class ComboBuilder {
        private String nombre = "Combo Cafetería";
        private Producto principal;
        private Producto acompanamiento;
        private Producto bebida;
        private Producto postre;
        private double descuentoCombo = 0.0;

        public ComboBuilder conNombre(String nombre) {
            if (nombre != null && !nombre.isBlank()) {
                this.nombre = nombre.trim();
            }
            return this;
        }

        public ComboBuilder conPrincipal(Producto principal) {
            this.principal = principal;
            return this;
        }

        public ComboBuilder conAcompanamiento(Producto acompanamiento) {
            this.acompanamiento = acompanamiento;
            return this;
        }

        public ComboBuilder conBebida(Producto bebida) {
            this.bebida = bebida;
            return this;
        }

        public ComboBuilder conPostre(Producto postre) {
            this.postre = postre;
            return this;
        }

        public ComboBuilder conDescuento(double descuentoCombo) {
            if (descuentoCombo < 0) {
                throw new IllegalArgumentException("El descuento del combo no puede ser negativo.");
            }
            this.descuentoCombo = descuentoCombo;
            return this;
        }

        public Combo build() {
            if (principal == null || bebida == null) {
                throw new IllegalStateException("Un combo requiere al menos un producto principal y una bebida.");
            }
            return new Combo(this);
        }
    }
}
