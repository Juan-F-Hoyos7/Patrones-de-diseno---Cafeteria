package com.cafeteria.gui;

import com.cafeteria.model.Pedido;
import com.cafeteria.model.Producto;
import com.cafeteria.model.ProductoBase;
import com.cafeteria.patterns.builder.Combo;
import com.cafeteria.patterns.observer.PedidoObserver;
import com.cafeteria.patterns.strategy.DescuentoDiaStrategy;
import com.cafeteria.patterns.strategy.HappyHourStrategy;
import com.cafeteria.patterns.strategy.PromocionStrategy;
import com.cafeteria.patterns.strategy.SinPromocionStrategy;
import com.cafeteria.service.CafeteriaService;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;

public class VentanaCafeteria extends JFrame implements PedidoObserver {
    private final CafeteriaService service;

    private final JComboBox<String> comboPedidos = new JComboBox<>();
    private final JComboBox<String> comboPromos = new JComboBox<>(new String[]{
            "Sin promoción",
            "Happy Hour (15%)",
            "Descuento Día (-$2.000)"
    });
    private final JComboBox<ProductoBase> comboProductos = new JComboBox<>();
    private final JCheckBox chkAdicion = new JCheckBox("Con adición (+$2.000)");

    private final JTextArea areaResumenPedido = new JTextArea(5, 50);
    private final JTextArea areaEventos = new JTextArea(8, 50);

    public VentanaCafeteria(CafeteriaService service) {
        super("Cafetería — Gestión de Pedidos");
        this.service = service;
        this.service.registrarObservadorGlobal(this);

        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        for (ProductoBase pb : service.getMenuBase()) {
            comboProductos.addItem(pb);
        }

        // --- Panel superior en 2 pasos sencillos ---
        JPanel panelArriba = new JPanel(new GridLayout(2, 1, 6, 6));

        JPanel paso1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paso1.setBorder(BorderFactory.createTitledBorder("1. Pedido y Estado"));
        JButton btnCrear = new JButton("Crear Pedido");
        JButton btnAvanzar = new JButton("Avanzar Estado");
        JButton btnCierre = new JButton("Cierre de Caja");

        paso1.add(new JLabel("Promoción:"));
        paso1.add(comboPromos);
        paso1.add(btnCrear);
        paso1.add(new JLabel("Pedido activo:"));
        paso1.add(comboPedidos);
        paso1.add(btnAvanzar);
        paso1.add(btnCierre);

        JPanel paso2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        paso2.setBorder(BorderFactory.createTitledBorder("2. Agregar Ítems al Pedido"));
        JButton btnAgregar = new JButton("Agregar Producto");
        JButton btnCombo = new JButton("Agregar Combo Rápido");

        paso2.add(new JLabel("Producto:"));
        paso2.add(comboProductos);
        paso2.add(chkAdicion);
        paso2.add(btnAgregar);
        paso2.add(btnCombo);

        panelArriba.add(paso1);
        panelArriba.add(paso2);

        // --- Panel central: detalle del pedido actual + historial/notificaciones ---
        JPanel panelCentro = new JPanel(new GridLayout(2, 1, 8, 8));

        areaResumenPedido.setEditable(false);
        areaResumenPedido.setBorder(BorderFactory.createTitledBorder("Detalle del Pedido Seleccionado"));

        areaEventos.setEditable(false);
        areaEventos.setBorder(BorderFactory.createTitledBorder("Notificaciones (Observer) y Cierre de Caja"));

        panelCentro.add(new JScrollPane(areaResumenPedido));
        panelCentro.add(new JScrollPane(areaEventos));

        add(panelArriba, BorderLayout.NORTH);
        add(panelCentro, BorderLayout.CENTER);

        // --- Eventos ---
        comboPedidos.addActionListener(e -> refrescarResumenPedido());

        btnCrear.addActionListener(e -> {
            Pedido creado = service.crearPedido(obtenerPromoSeleccionada());
            String itemId = String.valueOf(creado.getId());
            comboPedidos.addItem(itemId);
            comboPedidos.setSelectedItem(itemId);
            registrarEvento("Creado Pedido #" + creado.getId() + " con promo: " + creado.getPromocion().getNombre());
            refrescarResumenPedido();
        });

        btnAgregar.addActionListener(e -> {
            Pedido pedido = obtenerPedidoActual();
            ProductoBase base = (ProductoBase) comboProductos.getSelectedItem();
            if (pedido != null && base != null) {
                Producto item = base;
                if (chkAdicion.isSelected()) {
                    item = service.aplicarAdicion(item, "Adición especial", 2000.0);
                }
                service.agregarProductoAPedido(pedido.getId(), item);
                registrarEvento("Agregado a Pedido #" + pedido.getId() + ": " + item.getDescripcion());
                refrescarResumenPedido();
            }
        });

        btnCombo.addActionListener(e -> {
            Pedido pedido = obtenerPedidoActual();
            if (pedido != null && service.getMenuBase().size() >= 2) {
                ProductoBase principal = service.getMenuBase().get(3);
                ProductoBase bebida = service.getMenuBase().get(1);
                Combo combo = service.crearCombo("Combo Especial", principal, null, bebida, null, 2000.0);
                service.agregarProductoAPedido(pedido.getId(), combo);
                registrarEvento("Agregado a Pedido #" + pedido.getId() + ": " + combo.getDescripcion());
                refrescarResumenPedido();
            }
        });

        btnAvanzar.addActionListener(e -> {
            Pedido pedido = obtenerPedidoActual();
            if (pedido != null) {
                try {
                    service.avanzarEstadoPedido(pedido.getId());
                    refrescarResumenPedido();
                } catch (IllegalStateException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Estado final", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        btnCierre.addActionListener(e -> registrarEvento("\n" + service.generarCierreCaja()));
    }

    private PromocionStrategy obtenerPromoSeleccionada() {
        return switch (comboPromos.getSelectedIndex()) {
            case 1 -> new HappyHourStrategy(15.0);
            case 2 -> new DescuentoDiaStrategy("Día", 2000.0);
            default -> new SinPromocionStrategy();
        };
    }

    private Pedido obtenerPedidoActual() {
        Object sel = comboPedidos.getSelectedItem();
        if (sel == null) {
            JOptionPane.showMessageDialog(this, "Primero haz clic en 'Crear Pedido'.", "Sin pedido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return service.buscarPedido(Integer.parseInt(sel.toString()));
    }

    private void refrescarResumenPedido() {
        Object sel = comboPedidos.getSelectedItem();
        if (sel == null) {
            areaResumenPedido.setText("Aún no hay pedidos creados.");
            return;
        }
        Pedido p = service.buscarPedido(Integer.parseInt(sel.toString()));
        StringBuilder sb = new StringBuilder();
        sb.append("Pedido #").append(p.getId())
                .append(" | Estado: ").append(p.getEstadoNombre())
                .append(" | Promoción: ").append(p.getPromocion().getNombre()).append("\n");

        if (p.getProductos().isEmpty()) {
            sb.append("  - (Sin productos aún)\n");
        } else {
            for (Producto prod : p.getProductos()) {
                sb.append("  - ").append(prod.getDescripcion())
                        .append(" -> $").append(String.format("%.2f", prod.getPrecio())).append("\n");
            }
        }
        sb.append("Subtotal: $").append(String.format("%.2f", p.calcularSubtotal()))
                .append(" | Total con promo: $").append(String.format("%.2f", p.calcularTotal()));

        areaResumenPedido.setText(sb.toString());
    }

    private void registrarEvento(String texto) {
        areaEventos.append(texto + "\n");
    }

    @Override
    public void onEstadoCambiado(Pedido pedido) {
        SwingUtilities.invokeLater(() -> {
            registrarEvento("[Observer] Pedido #" + pedido.getId() + " cambió a " + pedido.getEstadoNombre());
            refrescarResumenPedido();
        });
    }
}