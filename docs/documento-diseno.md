# Documento de Diseño — Sistema de Pedidos para Cafetería / Restaurante
## 1. Alcance del sistema
* **Funcionalidades incluidas:**
  * Creación y gestión de pedidos con productos base personalizables mediante adiciones opcionales (modifican descripción y precio).
  * Armado de combos paso a paso con componentes obligatorios y opcionales.
  * Aplicación dinámica de promociones (sin promoción, descuento porcentual tipo *Happy Hour* o descuento fijo por día).
  * Gestión del ciclo de vida del pedido (`CREADO` -> `EN_PREPARACION` -> `LISTO` -> `ENTREGADO`) y notificación automática a cocina, mesero y caja.
  * Cierre de caja con resumen de pedidos entregados y total recaudado.
  * Ejecución por consola y por interfaz gráfica (Swing) compartiendo el mismo núcleo de negocio.
* **Funcionalidades excluidas:**
  * Persistencia en base de datos externa, gestión de usuarios/roles con autenticación y pasarela de pagos real, para priorizar la arquitectura en memoria, la separación núcleo-interfaz y la aplicación de patrones de diseño.
## 2. Requisitos del sistema

### Requisitos funcionales
* **RF-01:** Permitir crear un pedido asociándole una estrategia de promoción inicial o modificable.
* **RF-02:** Permitir agregar productos base con cero o más adiciones acumulables que actualicen descripción y precio.
* **RF-03:** Permitir armar combos paso a paso validando los elementos mínimos requeridos.
* **RF-04:** Permitir avanzar el estado de un pedido y notificar automáticamente a los observadores suscritos.
* **RF-05:** Calcular subtotales y totales aplicando la estrategia de promoción vigente y generar el cierre de caja.

### Requisitos no funcionales
* **RNF-01 (Desacoplamiento):** Separar la lógica de negocio (`model`, `patterns`, `service`) de la consola y Swing (`ui.console`, `ui.gui`).
* **RNF-02 (Extensibilidad):** Facilitar añadir nuevas adiciones, promociones o estados sin alterar el núcleo existente.
* **RNF-03 (Verificabilidad):** Contar con pruebas unitarias automatizadas para los patrones y transiciones clave.

---
## 3. Problemas de diseño, patrones elegidos y análisis

### 3.1 Personalización de productos con adiciones — Patrón **Decorator** (Estructural)
* **Problema de diseño:** Crear una clase hija por cada combinación de bebida o plato con extras genera explosión de clases y rigidez si cambian precios o descripciones.
* **Descripción con palabras propias:** Envuelve un `Producto` dentro de un decorador que añade su propio costo y texto al llamar a `getPrecio()` y `getDescripcion()`, manteniendo la misma interfaz `Producto`.
* **Roles:** `Producto` (`<<Component>>`), `ProductoBase` (`<<ConcreteComponent>>`), `ProductoDecorator` (`<<Decorator>>`), `AdicionDecorator` (`<<ConcreteDecorator>>`).
* **Alternativa descartada:** Subclases fijas por combinación o listas de booleanos en `ProductoBase`, descartadas por poca flexibilidad y violación de mantenibilidad al crecer las opciones.
* **Ventajas, desventajas y SOLID:** Permite combinar adiciones en cualquier orden y cantidad respetando **OCP** (nuevos decoradores sin modificar existentes) y **LSP** (todo decorador es un `Producto`). Como desventaja, anidar muchas capas puede hacer más larga la cadena de llamadas al inspeccionar detalles internos.

### 3.2 Armado de combos paso a paso — Patrón **Builder** (Creacional)
* **Problema de diseño:** Un constructor con múltiples parámetros opcionales y obligatorios (`principal`, `acompañamiento`, `bebida`, `postre`, `descuento`) resulta confuso y propenso a errores de orden o nulos inválidos.
* **Descripción con palabras propias:** Separa la construcción paso a paso de un `Combo` en un `ComboBuilder` fluido que valida las reglas mínimas antes de entregar el objeto final.
* **Roles:** `Combo` (`<<Product>>`), `Combo.ComboBuilder` (`<<Builder>>`), `CafeteriaService` (`<<Client>>`).
* **Alternativa descartada:** Constructores sobrecargados o setters directos en `Combo`, descartados porque dificultan validar que el combo esté completo al crearse y dejan objetos parcialmente armados.
* **Ventajas, desventajas y SOLID:** Mejora la legibilidad del código y centraliza las validaciones en `build()`, apoyando **SRP**. Como desventaja, añade clases/métodos adicionales para construir un producto compuesto.

### 3.3 Ciclo de vida del pedido — Patrón **State** (Comportamiento)
* **Problema de diseño:** Controlar las transiciones de estado de un pedido con cadenas `if/else` o `switch` repartidas dispersa las reglas y permite avanzar estados inválidos (como seguir avanzando después de `ENTREGADO`).
* **Descripción con palabras propias:** Cada estado del pedido es un objeto que sabe cuál es su nombre y a qué estado puede pasar al avanzar.
* **Roles:** `Pedido` (`<<Context>>`), `EstadoPedido` (`<<State>>`), `EstadoCreado`, `EstadoEnPreparacion`, `EstadoListo`, `EstadoEntregado` (`<<ConcreteState>>`).
* **Alternativa descartada:** Un `enum` con `switch` gigante en `Pedido`, descartado porque concentra toda la lógica de transición en un solo lugar y dificulta aislar comportamientos por estado.
* **Ventajas, desventajas y SOLID:** Aísla cada transición y protege estados terminales (**SRP** y **OCP**). Como desventaja, crea varias clases pequeñas para estados que en sistemas simples podrían ser mínimos.

### 3.4 Notificación por cambio de estado — Patrón **Observer** (Comportamiento)
* **Problema de diseño:** Hacer que `Pedido` llame directamente a consola, cocina, caja o ventanas acopla el dominio a la interfaz y complica agregar nuevos receptores.
* **Descripción con palabras propias:** `Pedido` mantiene una lista de `PedidoObserver` y les avisa automáticamente cada vez que cambia su estado interno.
* **Roles:** `Pedido` (`<<Subject>>`), `PedidoObserver` (`<<Observer>>`), `NotificadorConsolaObserver`, `VentanaCafeteria` (`<<ConcreteObserver>>`).
* **Alternativa descartada:** Llamadas directas desde el servicio o la UI tras cada acción, descartadas porque no garantizan que cualquier cambio de estado dispare la notificación automáticamente.
* **Ventajas, desventajas y SOLID:** Desacopla el núcleo de quienes reaccionan al evento (**DIP** y **OCP**). Como desventaja, el flujo de eventos puede volverse menos obvio si hay muchos observadores registrados.

### 3.5 Promociones por horario o día — Patrón **Strategy** (Comportamiento)
* **Problema de diseño:** Calcular descuentos con condicionales dentro de `Pedido` obliga a editar la clase central cada vez que se agrega o cambia una promoción.
* **Descripción con palabras propias:** Encapsula cada regla de descuento detrás de `PromocionStrategy`, de modo que `Pedido` delega el cálculo final a la estrategia activa.
* **Roles:** `Pedido` (`<<Context>>`), `PromocionStrategy` (`<<Strategy>>`), `SinPromocionStrategy`, `HappyHourStrategy`, `DescuentoDiaStrategy` (`<<ConcreteStrategy>>`).
* **Alternativa descartada:** Condicionales `if/else` dentro de `calcularTotal()`, descartados porque mezclan cálculo de totales con reglas promocionales variables.
* **Ventajas, desventajas y SOLID:** Facilita probar cada promoción por separado y agregar nuevas reglas sin tocar `Pedido` (**SRP**, **OCP** y **DIP**). Como desventaja, requiere instanciar o seleccionar la estrategia correspondiente.

---
## 4. Arquitectura general
El proyecto se divide en:
* **`model` y `patterns`:** Entidades (`Producto`, `ProductoBase`, `Pedido`) y patrones (`decorator`, `builder`, `state`, `observer`, `strategy`).
* **`service` (`CafeteriaService`):** Punto central de operaciones (menú, adiciones, combos, pedidos, estados y cierre de caja).
* **`ui.console` y `ui.gui`:** Interfaz de consola e interfaz gráfica Swing que consumen `CafeteriaService` sin duplicar reglas de negocio.

---
## 5. Diagramas UML

* **Casos de uso:** 
* **Paquetes:** <img width="1173" height="622" alt="image" src="https://github.com/user-attachments/assets/753e2efd-bd32-4541-a1ae-ab6a0db53a33" />
* **Clases general:** <img width="1376" height="697" alt="image" src="https://github.com/user-attachments/assets/96383182-16b0-4207-bde2-cf230f0a1878" />
* **Estados del pedido:** 
* **Secuencia (flujo principal):** 
* **Clases por patrón:** 

---
