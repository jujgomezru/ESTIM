# Steam Frontend – React + Vite

This project represents the official frontend of a digital distribution platform inspired by Steam, built with React and Vite under a modular, scalable, component-oriented architecture. The application consumes REST services provided by a backend implemented in Java Spring Boot running on localhost:8080.

## Requirements
- Node.js (recommended LTS version)
- Code editor compatible with JS/React (VSCode suggested)
- Modern browser with ES6+ support

## Project Structure
src/
- app/
  - App.jsx: Central routing point and base layout
  - main.jsx: App initialization and DOM mounting
  - routes.jsx: Platform route table
- api/
  - apiClient.js: Centralized HTTP client, error handling, response mapping
  - endpoints.js: Unified backend endpoint definitions
- components/
  - Badge.jsx
  - Button.jsx
  - Footer.jsx
  - GameCard.jsx
  - Header.jsx
  - Input.jsx
  - Layout.jsx
  - LoadingSpinner.jsx
- features/
  - auth/
  - cart/
  - games/
  - wishlist/
  - library/
  - simpsons/
  - support/
- styles/
  - globals.css: Global styles and base definitions

## Backend Communication
Flow:
Page.jsx → service.js → apiClient.js + endpoints.js → Java Backend (localhost:8080)

### apiClient.js
- Standardizes all HTTP requests
- Handles responses and errors
- Avoids duplicated logic across modules

### endpoints.js
Centralized contract for backend routes.

Example:
ENDPOINTS = {
  AUTH: "/auth",
  GAMES: "/games",
  CART: "/cart",
  WISHLIST: "/wishlist",
  LIBRARY: "/library",
  SUPPORT: "/support"
};

### Services per feature
- Prepare and structure requests
- Interact with apiClient.js
- Keep communication logic separate from the presentation layer

## License
Project developed for professional and educational purposes. Its use, distribution, or modification is subject to the organization’s internal policies.
