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
