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
