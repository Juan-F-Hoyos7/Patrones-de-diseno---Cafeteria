package com.cafeteria.service;

import com.cafeteria.model.Pedido;
import com.cafeteria.model.Producto;
import com.cafeteria.model.ProductoBase;
import com.cafeteria.patterns.builder.Combo;
import com.cafeteria.patterns.builder.ComboBuilder;
import com.cafeteria.patterns.decorator.AdicionDecorator;
import com.cafeteria.patterns.observer.PedidoObserver;
import com.cafeteria.patterns.strategy.PromocionStrategy;
import com.cafeteria.patterns.strategy.SinPromocionStrategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CafeteriaService {
    private final List<ProductoBase> menuBase = new ArrayList<>();
    private final Map<String, Double> adicionesDisponibles = new LinkedHashMap<>();
    private final Map<Integer, Pedido> pedidos = new LinkedHashMap<>();
    private final List<PedidoObserver> observadoresGlobales = new ArrayList<>();
    private int contadorPedidos = 1;

    public CafeteriaService() {
        cargarCatalogoInicial();
    }

    private void cargarCatalogoInicial() {
        menuBase.add(new ProductoBase("Café Americano", 5500));
        menuBase.add(new ProductoBase("Capuchino", 7500));
        menuBase.add(new ProductoBase("Croissant de Mantequilla", 6000));
        menuBase.add(new ProductoBase("Sándwich de Pollo", 14500));
        menuBase.add(new ProductoBase("Jugo Natural de Naranja", 6500));
        menuBase.add(new ProductoBase("Brownie de Chocolate", 7000));

        adicionesDisponibles.put("Shot extra de espresso", 2500.0);
        adicionesDisponibles.put("Leche de almendras", 2000.0);
        adicionesDisponibles.put("Queso cheddar", 3000.0);
        adicionesDisponibles.put("Salsa de caramelo", 1800.0);
    }

    public List<ProductoBase> getMenuBase() {
        return Collections.unmodifiableList(menuBase);
    }

    public Map<String, Double> getAdicionesDisponibles() {
        return Collections.unmodifiableMap(adicionesDisponibles);
    }

    public void registrarObservadorGlobal(PedidoObserver observer) {
        if (observer != null && !observadoresGlobales.contains(observer)) {
            observadoresGlobales.add(observer);
            for (Pedido p : pedidos.values()) {
                p.agregarObservador(observer);
            }
        }
    }

    public Pedido crearPedido(PromocionStrategy promocion) {
        PromocionStrategy promo = (promocion != null) ? promocion : new SinPromocionStrategy();
        Pedido pedido = new Pedido(contadorPedidos++, promo);
        for (PedidoObserver obs : observadoresGlobales) {
            pedido.agregarObservador(obs);
        }
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    public Pedido buscarPedido(int id) {
        Pedido pedido = pedidos.get(id);
        if (pedido == null) {
            throw new IllegalArgumentException("No existe un pedido con ID #" + id);
        }
        return pedido;
    }

    public List<Pedido> getPedidos() {
        return new ArrayList<>(pedidos.values());
    }

    public Producto aplicarAdicion(Producto producto, String nombreAdicion, double costo) {
        return new AdicionDecorator(producto, nombreAdicion, costo);
    }

    public Combo crearCombo(String nombre, Producto principal, Producto acompanamiento,
                            Producto bebida, Producto postre, double descuento) {
        return new ComboBuilder()
                .conNombre(nombre)
                .conPrincipal(principal)
                .conAcompanamiento(acompanamiento)
                .conBebida(bebida)
                .conPostre(postre)
                .conDescuento(descuento)
                .build();
    }

    public void agregarProductoAPedido(int idPedido, Producto producto) {
        Pedido pedido = buscarPedido(idPedido);
        pedido.agregarProducto(producto);
    }

    public void cambiarPromocionPedido(int idPedido, PromocionStrategy promocion) {
        Pedido pedido = buscarPedido(idPedido);
        pedido.setPromocion(promocion);
    }

    public void avanzarEstadoPedido(int idPedido) {
        Pedido pedido = buscarPedido(idPedido);
        pedido.avanzarEstado();
    }

    public String generarCierreCaja() {
        int totalPedidos = pedidos.size();
        int entregados = 0;
        double totalRecaudado = 0.0;

        for (Pedido p : pedidos.values()) {
            if ("ENTREGADO".equals(p.getEstadoNombre())) {
                entregados++;
                totalRecaudado += p.calcularTotal();
            }
        }

        return "=== CIERRE DE CAJA ===\n"
                + "Total pedidos registrados: " + totalPedidos + "\n"
                + "Pedidos entregados: " + entregados + "\n"
                + "Total recaudado (entregados): $" + String.format("%.2f", totalRecaudado);
    }
}