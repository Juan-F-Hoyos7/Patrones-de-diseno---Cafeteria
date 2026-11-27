package com.cafeteria.patterns.state;

import com.cafeteria.model.Pedido;

public class EstadoListo implements EstadoPedido {
    @Override
    public String getNombre() {
        return "LISTO";
    }

    @Override
    public void avanzar(Pedido pedido) {
        pedido.setEstadoInterno(new EstadoEntregado());
    }
}
