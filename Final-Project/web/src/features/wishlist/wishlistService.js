import { apiGet, apiPost, apiDelete } from "../../api/apiClient";

// Obtener toda la wishlist del usuario
export async function getWishlist() {
  return await apiGet("/wishlist");
}

// Agregar un juego a la wishlist
export async function addToWishlist(gameId) {
  return await apiPost("/wishlist", { gameId });
}

// Eliminar un juego de la wishlist
export async function removeFromWishlist(gameId) {
  return await apiDelete(`/wishlist/${gameId}`);
}

// Verificar si un juego está en la wishlist
export async function isInWishlist(gameId) {
  return await apiGet(`/wishlist/check/${gameId}`);
}

// Limpiar toda la wishlist
export async function clearWishlist() {
  return await apiDelete("/wishlist/clear");
}

// Obtener cantidad de items en wishlist
export async function getWishlistCount() {
  return await apiGet("/wishlist/count");
}
