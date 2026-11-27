import com.cafeteria.console.ConsolaCafeteria;
import com.cafeteria.gui.VentanaCafeteria;
import com.cafeteria.service.CafeteriaService;

import javax.swing.SwingUtilities;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        CafeteriaService service = new CafeteriaService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== BIENVENIDO AL SISTEMA DE PEDIDOS ===");
        System.out.println("1. Modo Consola");
        System.out.println("2. Modo Interfaz Gráfica (Swing)");
        System.out.print("Elige cómo iniciar: ");

        String opcion = scanner.nextLine().trim();
        if ("2".equals(opcion)) {
            SwingUtilities.invokeLater(() -> new VentanaCafeteria(service).setVisible(true));
        } else {
            new ConsolaCafeteria(service).iniciar();
        }
    }
}
