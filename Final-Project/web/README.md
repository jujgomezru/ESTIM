
# STEAM / GAMESTORE — Frontend (React + Vite)

## Summary
React + Vite frontend for the (Steam/GAMESTORE) platform. This README covers installation, structure, workflow, testing, and common commands.

---

## Requirements
- Node.js (v16+ recommended)
- Code editor (VSCode, etc.)
- Modern browser

---

## Quick Installation

```bash
# Install dependencies
npm install
npm install react-router-dom

# Development server
npm run dev

# Production build
npm run build

# Build preview
npm run preview
````

---

## Project Structure (summary)

```
web/
├── src/
│   ├── app/
│   │   ├── App.jsx            ← starts the app
│   │   ├── main.jsx           ← mounts App to the DOM
│   │   └── routes.jsx         ← app routes
│   ├── api/
│   │   ├── apiClient.js       ← backend request handler
│   │   └── endpoints.js       ← backend URLs
│   ├── components/            ← reusable components
│   │   ├── Button.jsx
│   │   └── __test__/          ← component tests
│   ├── hooks/
│   │   └── useFetch.js
│   ├── features/
│   │   └── auth/
│   │   │  ├── LoginPage.jsx
│   │   │  ├── RegisterPage.jsx
│   │   │  └── .../
│   │   └── .../
│   ├── styles/
│   │   └── globals.css
│   └── setupTests.js          ← global test configuration
├── jest.config.js
├── babel.config.cjs
├── vite.config.js
└── package.json
```

---

## General Flow (how layers communicate)

```
src/app/main.jsx
  ↓ (app starts)
src/app/App.jsx
  ↓ (loads routes)
src/app/routes.jsx
  ↓ (renders page based on URL)
src/features/.../Page.jsx
  ↓ (renders UI and calls service)
src/features/.../service.js
  ↓ (prepares request)
src/api/apiClient.js  +  src/api/endpoints.js
  ↓ (fetch to backend)
BACKEND JAVA < localhost:8080 >
  ↓ (JSON response)
src/features/.../Page.jsx
  ↓ (updates UI)
```

---

## Testing

### Run tests

```bash
npm test
```

### Useful commands

```bash
# Watch mode (development)
npm test -- --watch

# Run once (no watch)
npm test -- --watchAll=false

# Coverage
npm test -- --coverage

# Specific test file
npm test -- src/components/__test__/Button.test.jsx
```

### Expected output (example)

```
 PASS  src/components/__test__/Button.test.jsx
 PASS  src/components/__test__/GameCard.test.jsx
 PASS  src/components/__test__/Header.test.jsx

Test Suites: 3 passed, 3 total
Tests:       15 passed, 15 total
Snapshots:   0 total
Time:        ~2s
```

### Included tests (location: `src/components/__test__/`)

* `Button.test.jsx` — 5 tests
* `GameCard.test.jsx` — 6 tests
* `Header.test.jsx` — 4 tests
  **TOTAL:** 15 tests

### Testing configuration

* Jest
* @testing-library/react
* @testing-library/jest-dom
* babel-jest

Relevant files: `jest.config.js`, `babel.config.cjs`, `src/setupTests.js`

---

## Troubleshooting (common issues)

```bash
# Reinstall dependencies
rm -rf node_modules package-lock.json
npm install

# Clear Jest cache
npm test -- --clearCache

# If jsdom environment is missing
npm install --save-dev jest-environment-jsdom

# Check Node version
node --version  # should be v16+
```

If hook-related errors persist: verify `src/setupTests.js`, clear cache, and reinstall `node_modules`.

---

## Development & lint

```bash
# Dev server with HMR
npm run dev

# Build
npm run build

# Preview
npm run preview

# Lint
npm run lint
```

---

## Notes and conventions

* Each feature must have its own `Page.jsx` (view) and `service.js` (logic/requests).
* All backend communication **goes through** `apiClient.js`.
* Centralize backend routes in `endpoints.js`.
* The described flow applies to all features.


