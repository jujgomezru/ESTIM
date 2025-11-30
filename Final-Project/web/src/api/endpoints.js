import { searchGames } from "../features/games/gamesService";

// me dice que endpoints puede utilizar
export const ENDPOINTS = {
  REGISTER: "/auth/register", // endpoint de la api
  LOGIN: "/auth/login",
  CARD: "/card",
  CART: "/shopping_cart",
  ADD: "/shopping_cart/items/{gameId}",
  REMOVE: "/shopping_cart/items/{gameId}",
  TOTAL: "/shopping_cart/total",
  CLEAR: "/shopping_cart/clear",
  GAMES: "/games/",
  SEARCH: "/games/search/",
  GENRE: "/games/search/genre/",
  POPULAR: "/games/popular",
  RECENT: "/games/recent",
  SEED: "/admin/seed-data",
  SIMPSON_CHARACTER: "https://thesimpsonsapi.com/api/characters/1",//simpsons
};
