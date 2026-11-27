package com.cafeteria.patterns.builder;

import com.cafeteria.model.Producto;

public class ComboBuilder {
    private final Combo.ComboBuilder delegate = new Combo.ComboBuilder();

    public ComboBuilder conNombre(String nombre) {
        delegate.conNombre(nombre);
        return this;
    }

    public ComboBuilder conPrincipal(Producto principal) {
        delegate.conPrincipal(principal);
        return this;
    }

    public ComboBuilder conAcompanamiento(Producto acompanamiento) {
        delegate.conAcompanamiento(acompanamiento);
        return this;
    }

    public ComboBuilder conBebida(Producto bebida) {
        delegate.conBebida(bebida);
        return this;
    }

    public ComboBuilder conPostre(Producto postre) {
        delegate.conPostre(postre);
        return this;
    }

    public ComboBuilder conDescuento(double descuentoCombo) {
        delegate.conDescuento(descuentoCombo);
        return this;
    }

    public Combo build() {
        return delegate.build();
    }
}