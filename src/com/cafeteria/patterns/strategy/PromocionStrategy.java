package com.cafeteria.patterns.strategy;

public interface PromocionStrategy {
    String getNombre();
    double aplicarDescuento(double subtotal);
}