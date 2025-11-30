import { apiGet, apiPost, apiDelete } from "../../api/apiClient";

// Obtener todos los items del carrito
export async function getCart() {
  return await apiGet("/shopping_cart");
}

// Agregar un juego al carrito
export async function addToCart(gameId) {
  return await apiPost(`/shopping_cart/items/${gameId}`);
}

// Eliminar un juego del carrito
export async function removeFromCart(gameId) {
  return await apiDelete(`/shopping_cart/${gameId}`);
}

// Vaciar todo el carrito
export async function clearCart() {
  return await apiDelete("/shopping_cart/clear");
}

// Actualizar cantidad de un item (opcional, por si lo necesitas)
export async function updateCartItem(gameId, quantity) {
  return await apiPost("/shopping_cart/update", { gameId, quantity });
}

// Procesar compra / checkout
export async function checkout() {
  return await apiPost("/shopping_cart/checkout", {});
}