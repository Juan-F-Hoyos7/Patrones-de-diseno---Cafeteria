# Sistema de Pedidos - Cafetería

Aplicación en Java para gestionar pedidos de cafetería mediante un núcleo compartido (`CafeteriaService` y modelo de dominio) con soporte para **consola**, **interfaz gráfica (Swing)** y **pruebas unitarias con JUnit 5**.

## Patrones de diseño implementados

* **Builder (`Combo`, `Combo.ComboBuilder`):** Construcción paso a paso de combos validando componentes obligatorios y aplicando descuentos.
* **Decorator (`ProductoDecorator`, `AdicionDecorator`):** Agrega adiciones extra de forma dinámica sobre cualquier `Producto`, acumulando descripción y precio.
* **State (`EstadoPedido`, `EstadoCreado`, `EstadoEnPreparacion`, `EstadoListo`, `EstadoEntregado`):** Controla el ciclo de vida del pedido y las reglas según su estado actual.
* **Observer (`PedidoObserver`, `NotificadorConsolaObserver`, `ConsolaCafeteria`, `VentanaCafeteria`):** Notifica cambios de estado en los pedidos a observadores registrados.
* **Strategy (`PromocionStrategy`, `SinPromocionStrategy`, `HappyHourStrategy`, `DescuentoDiaStrategy`):** Permite intercambiar reglas de descuento sin alterar la lógica del pedido.

## Estructura principal

```text
src/
├── Main.java
└── com/cafeteria/
    ├── console/
    │   └── ConsolaCafeteria.java
    ├── gui/
    │   └── VentanaCafeteria.java
    ├── model/
    │   ├── Pedido.java
    │   ├── Producto.java
    │   └── ProductoBase.java
    ├── patterns/
    │   ├── builder/
    │   ├── decorator/
    │   ├── observer/
    │   ├── state/
    │   └── strategy/
    └── service/
        └── CafeteriaService.java
