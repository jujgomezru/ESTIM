import { apiGet } from "../../api/apiClient";

// Obtener todos los juegos de la tienda
export async function getAllGames() {
  return await apiGet("/games");
}

// Obtener juegos por categoría
export async function getGamesByCategory(category) {
  return await apiGet(`/games/category/${category}`);
}

// Obtener un juego específico
export async function getGameById(id) {
  return await apiGet(`/games/${id}`);
}

// Buscar juegos
export async function searchGames(query) {
  return await apiGet(`/games/search?q=${query}`);
}