package com.cafeteria.patterns.observer;

import com.cafeteria.model.Pedido;

public class NotificadorConsolaObserver implements PedidoObserver {
    private final String rolDestino;

    public NotificadorConsolaObserver(String rolDestino) {
        this.rolDestino = (rolDestino != null && !rolDestino.isBlank()) ? rolDestino.trim() : "Sistema";
    }

    @Override
    public void onEstadoCambiado(Pedido pedido) {
        System.out.println("[" + rolDestino + "] Pedido #" + pedido.getId()
                + " cambió de estado a: " + pedido.getEstadoNombre());
    }
}