package com.cafeteria.patterns.state;

import com.cafeteria.model.Pedido;

public class EstadoEntregado implements EstadoPedido {
    @Override
    public String getNombre() {
        return "ENTREGADO";
    }

    @Override
    public void avanzar(Pedido pedido) {
        throw new IllegalStateException("El pedido #" + pedido.getId() + " ya fue ENTREGADO y no puede avanzar más.");
    }
}
