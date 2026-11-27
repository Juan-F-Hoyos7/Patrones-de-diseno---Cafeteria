
package com.cafeteria.model;

import com.cafeteria.patterns.observer.PedidoObserver;
import com.cafeteria.patterns.state.EstadoCreado;
import com.cafeteria.patterns.state.EstadoPedido;
import com.cafeteria.patterns.strategy.PromocionStrategy;
import com.cafeteria.patterns.strategy.SinPromocionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private final int id;
    private final List<Producto> productos = new ArrayList<>();
    private final List<PedidoObserver> observadores = new ArrayList<>();
    private EstadoPedido estado;
    private PromocionStrategy promocion;

    public Pedido(int id) {
        this(id, new SinPromocionStrategy());
    }

    public Pedido(int id, PromocionStrategy promocion) {
        this.id = id;
        this.promocion = (promocion != null) ? promocion : new SinPromocionStrategy();
        this.estado = new EstadoPedido();
    }

    public int getId() {
        return id;
    }

    public void agregarProducto(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo.");
        }
        productos.add(producto);
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public double calcularSubtotal() {
        double subtotal = 0.0;
        for (Producto p : productos) {
            subtotal += p.getPrecio();
        }
        return subtotal;
    }

    public double calcularTotal() {
        return promocion.aplicarDescuento(calcularSubtotal());
    }

    public PromocionStrategy getPromocion() {
        return promocion;
    }

    public void setPromocion(PromocionStrategy promocion) {
        if (promocion == null) {
            throw new IllegalArgumentException("La estrategia de promoción no puede ser nula.");
        }
        this.promocion = promocion;
    }

    public String getEstadoNombre() {
        return estado.getNombre();
    }

    public com.cafeteria.model.EstadoPedido getEstado() {
        return estado;
    }

    public void avanzarEstado() {
        estado.avanzar(this);
    }

    public void setEstadoInterno(EstadoPedido nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        this.estado = nuevoEstado;
        notificarObservadores();
    }

    public void agregarObservador(PedidoObserver observer) {
        if (observer != null && !observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    public void eliminarObservador(PedidoObserver observer) {
        observadores.remove(observer);
    }

    private void notificarObservadores() {
        for (PedidoObserver obs : new ArrayList<>(observadores)) {
            obs.onEstadoCambiado(this);
        }
    }
}
