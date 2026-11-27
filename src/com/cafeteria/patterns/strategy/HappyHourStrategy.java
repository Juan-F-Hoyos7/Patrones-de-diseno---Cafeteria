package com.cafeteria.patterns.strategy;

public class HappyHourStrategy implements PromocionStrategy {
    private final double porcentajeDescuento;

    public HappyHourStrategy(double porcentajeDescuento) {
        if (porcentajeDescuento < 0 || porcentajeDescuento > 100) {
            throw new IllegalArgumentException("El porcentaje de descuento debe estar entre 0 y 100.");
        }
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public String getNombre() {
        return "Happy Hour (" + porcentajeDescuento + "%)";
    }

    @Override
    public double aplicarDescuento(double subtotal) {
        double montoDescuento = subtotal * (porcentajeDescuento / 100.0);
        return Math.max(0.0, subtotal - montoDescuento);
    }
}