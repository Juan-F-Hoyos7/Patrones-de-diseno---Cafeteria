package com.cafeteria.patterns.state;

import com.cafeteria.model.Pedido;

public class EstadoEnPreparacion implements EstadoPedido {
    @Override
    public String getNombre() {
        return "EN_PREPARACION";
    }

    @Override
    public void avanzar(Pedido pedido) {
        pedido.setEstadoInterno(new EstadoListo());
    }
}