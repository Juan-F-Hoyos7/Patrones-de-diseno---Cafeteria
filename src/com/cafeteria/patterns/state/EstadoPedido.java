package com.cafeteria.patterns.state;

import com.cafeteria.model.Pedido;

public interface EstadoPedido {
    String getNombre();
    void avanzar(Pedido pedido);
}
