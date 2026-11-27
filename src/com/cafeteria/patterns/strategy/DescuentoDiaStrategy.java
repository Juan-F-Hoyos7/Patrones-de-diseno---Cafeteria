package com.cafeteria.patterns.strategy;

public class DescuentoDiaStrategy implements PromocionStrategy {
    private final String nombreDia;
    private final double montoFijo;

    public DescuentoDiaStrategy(String nombreDia, double montoFijo) {
        if (montoFijo < 0) {
            throw new IllegalArgumentException("El monto de descuento no puede ser negativo.");
        }
        this.nombreDia = (nombreDia != null && !nombreDia.isBlank()) ? nombreDia.trim() : "Día";
        this.montoFijo = montoFijo;
    }

    @Override
    public String getNombre() {
        return "Descuento " + nombreDia + " (-$" + String.format("%.2f", montoFijo) + ")";
    }

    @Override
    public double aplicarDescuento(double subtotal) {
        return Math.max(0.0, subtotal - montoFijo);
    }
}