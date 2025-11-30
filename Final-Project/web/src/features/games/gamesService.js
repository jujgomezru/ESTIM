import { apiGet } from "../../api/apiClient";

// Obtener todos los juegos de la tienda
export async function getAllGames() {
  const response = await fetch("http://localhost:8000/games/");
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`); 
  }
  return await response.json();
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
  const response = await fetch(`http://localhost:8000/games/search/?q=${query}`);
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`);
  }
  return await response.json();
}