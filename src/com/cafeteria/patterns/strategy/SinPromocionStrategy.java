package com.cafeteria.patterns.strategy;

public class SinPromocionStrategy implements PromocionStrategy {
    @Override
    public String getNombre() {
        return "Sin promoción";
    }

    @Override
    public double aplicarDescuento(double subtotal) {
        return Math.max(0.0, subtotal);
    }
}