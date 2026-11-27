package com.cafeteria.console;

import com.cafeteria.model.Pedido;
import com.cafeteria.model.Producto;
import com.cafeteria.model.ProductoBase;
import com.cafeteria.patterns.builder.Combo;
import com.cafeteria.patterns.observer.NotificadorConsolaObserver;
import com.cafeteria.patterns.strategy.DescuentoDiaStrategy;
import com.cafeteria.patterns.strategy.HappyHourStrategy;
import com.cafeteria.patterns.strategy.PromocionStrategy;
import com.cafeteria.patterns.strategy.SinPromocionStrategy;
import com.cafeteria.service.CafeteriaService;

import java.util.List;
import java.util.Scanner;

public class ConsolaCafeteria {
    private final CafeteriaService service;
    private final Scanner scanner = new Scanner(System.in);

    public ConsolaCafeteria(CafeteriaService service) {
        this.service = service;
        this.service.registrarObservadorGlobal(new NotificadorConsolaObserver("Cocina/Caja"));
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarMenu();
            opcion = leerEntero("Elige una opción: ");
            try {
                switch (opcion) {
                    case 1 -> crearPedido();
                    case 2 -> agregarProductoConAdicion();
                    case 3 -> armarCombo();
                    case 4 -> avanzarEstado();
                    case 5 -> verPedidos();
                    case 6 -> System.out.println("\n" + service.generarCierreCaja());
                    case 0 -> System.out.println("Saliendo de la consola...");
                    default -> System.out.println("Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (opcion != 0);
    }

    private void mostrarMenu() {
        System.out.println("\n--- SISTEMA CAFETERÍA (CONSOLA) ---");
        System.out.println("1. Crear pedido");
        System.out.println("2. Agregar producto (con adición opcional)");
        System.out.println("3. Armar combo");
        System.out.println("4. Avanzar estado de pedido");
        System.out.println("5. Listar pedidos");
        System.out.println("6. Cierre de caja");
        System.out.println("0. Volver / Salir");
    }

    private void crearPedido() {
        PromocionStrategy promo = elegirPromocion();
        Pedido pedido = service.crearPedido(promo);
        System.out.println("Pedido #" + pedido.getId() + " creado (" + pedido.getEstadoNombre()
                + ") con promo: " + pedido.getPromocion().getNombre());
    }

    private void agregarProductoConAdicion() {
        int idPedido = leerEntero("ID del pedido: ");
        Producto seleccionado = elegirProductoBase();
        String addExtra = leerTexto("¿Agregar adición? (s/n): ");
        if (addExtra.equalsIgnoreCase("s")) {
            String nombreAdd = leerTexto("Nombre de la adición: ");
            double costoAdd = leerDouble("Costo de la adición: ");
            seleccionado = service.aplicarAdicion(seleccionado, nombreAdd, costoAdd);
        }
        service.agregarProductoAPedido(idPedido, seleccionado);
        System.out.println("Agregado: " + seleccionado.getDescripcion() + " ($" + seleccionado.getPrecio() + ")");
    }

    private void armarCombo() {
        int idPedido = leerEntero("ID del pedido: ");
        String nombreCombo = leerTexto("Nombre del combo: ");
        System.out.println("Selecciona producto principal:");
        Producto principal = elegirProductoBase();
        System.out.println("Selecciona bebida:");
        Producto bebida = elegirProductoBase();
        double descuento = leerDouble("Descuento del combo: ");
        Combo combo = service.crearCombo(nombreCombo, principal, null, bebida, null, descuento);
        service.agregarProductoAPedido(idPedido, combo);
        System.out.println("Combo agregado: " + combo.getDescripcion() + " ($" + combo.getPrecio() + ")");
    }

    private void avanzarEstado() {
        int idPedido = leerEntero("ID del pedido a avanzar: ");
        service.avanzarEstadoPedido(idPedido);
    }

    private void verPedidos() {
        List<Pedido> lista = service.getPedidos();
        if (lista.isEmpty()) {
            System.out.println("No hay pedidos creados.");
            return;
        }
        for (Pedido p : lista) {
            System.out.println("Pedido #" + p.getId()
                    + " | Estado: " + p.getEstadoNombre()
                    + " | Promo: " + p.getPromocion().getNombre()
                    + " | Subtotal: $" + String.format("%.2f", p.calcularSubtotal())
                    + " | Total: $" + String.format("%.2f", p.calcularTotal()));
        }
    }

    private PromocionStrategy elegirPromocion() {
        System.out.println("Promoción: 1) Sin promoción  2) Happy Hour 15%  3) Descuento Día $2000");
        int op = leerEntero("Opción: ");
        return switch (op) {
            case 2 -> new HappyHourStrategy(15.0);
            case 3 -> new DescuentoDiaStrategy("Jueves", 2000.0);
            default -> new SinPromocionStrategy();
        };
    }

    private ProductoBase elegirProductoBase() {
        List<ProductoBase> menu = service.getMenuBase();
        for (int i = 0; i < menu.size(); i++) {
            ProductoBase pb = menu.get(i);
            System.out.println((i + 1) + ". " + pb.getDescripcion() + " ($" + pb.getPrecio() + ")");
        }
        int idx = leerEntero("Elige número: ") - 1;
        if (idx < 0 || idx >= menu.size()) {
            throw new IllegalArgumentException("Producto fuera de rango.");
        }
        return menu.get(idx);
    }

    private String leerTexto(String msg) {
        System.out.print(msg);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número entero válido.");
            }
        }
    }

    private double leerDouble(String msg) {
        while (true) {
            try {
                System.out.print(msg);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingresa un número válido.");
            }
        }
    }
}