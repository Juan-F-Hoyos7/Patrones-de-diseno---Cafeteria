package com.cafeteria.patterns.state;

import com.cafeteria.model.Pedido;

public class EstadoCreado implements EstadoPedido {
    @Override
    public String getNombre() {
        return "CREADO";
    }

    @Override
    public void avanzar(Pedido pedido) {
        pedido.setEstadoInterno(new EstadoEnPreparacion());
    }
}
