package com.cafeteria.patterns.observer;

import com.cafeteria.model.Pedido;

public interface PedidoObserver {
    void onEstadoCambiado(Pedido pedido);
}