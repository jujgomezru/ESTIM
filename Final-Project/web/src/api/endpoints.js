// src/api/endpoints.js

// me dice que endpoints puede utilizar
export const ENDPOINTS = {
  REGISTER: "/auth/register", // endpoint de la api
  LOGIN: "/auth/login",
  CARD: "/card",

  // 📚 Library endpoints
  MY_LIBRARY: "/me/library",                    // GET, POST
  myLibraryEntry: (gameId) => `/me/library/${gameId}`, // PATCH /me/library/{gameId}

  // Simpsons
  SIMPSON_CHARACTER: "https://thesimpsonsapi.com/api/characters/1",
};
