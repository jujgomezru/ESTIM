# GAMESTORE - Frontend Setup Guide

React + Vite application para la plataforma GAMESTORE.

## Instalación Rápida

```bash
# 1. Clonar repositorio
git clone <tu-repositorio>
cd ESTIM/Final-Project/web

# 2. Instalar dependencias
npm install

# 3. Ejecutar en desarrollo
npm run dev

# 4. Build para producción
npm run build
```

## Testing

### Ejecutar Todos los Tests
```bash
npm test
```

**Resultado Esperado:**
```
Test Suites: 3 passed, 3 total
Tests:       15 passed, 15 total
```

### Otros Comandos de Testing
```bash
# Tests en modo watch
npm test -- --watch

# Tests con cobertura
npm test -- --coverage

# Tests sin watch (una sola ejecución)
npm test -- --watchAll=false

# Test de un archivo específico
npm test -- src/components/__test__/Button.test.jsx
```

## Tests Disponibles

Los tests están organizados en `src/components/__test__/`:

| Archivo | Tests | Estado |
|---------|-------|--------|
| Button.test.jsx | 5 | ✅ PASS |
| GameCard.test.jsx | 6 | ✅ PASS |
| Header.test.jsx | 4 | ✅ PASS |
| **TOTAL** | **15** | **✅ PASS** |

### Descripción de Tests

**Button Component Tests:**
- Renderiza con children
- Callback onClick funciona
- Estado disabled funciona
- Spinner en loading
- Posicionamiento de icon

**GameCard Component Tests:**
- Renderiza título del juego
- Renderiza imagen del juego
- Muestra precio correcto
- Muestra badge de descuento
- Renderiza categorías
- Renderiza medios (screenshots)

**Header Component Tests:**
- Renderiza logo
- Renderiza items de navegación
- Input de búsqueda funciona
- Icon de carrito visible

## Requisitos Previos

- Node.js v16+
- npm o yarn

## Estructura del Proyecto

```
web/
├── src/
│   ├── components/
│   │   ├── __test__/           # Archivos de test
│   │   ├── Button.jsx
│   │   ├── GameCard.jsx
│   │   ├── Header.jsx
│   │   └── ...
│   ├── app/
│   ├── features/
│   ├── styles/
│   ├── api/
│   ├── main.jsx
│   └── setupTests.js           # Config de tests
├── jest.config.js              # Configuración Jest
├── babel.config.cjs            # Configuración Babel
├── vite.config.js
└── package.json
```

## Configuración de Testing

El proyecto usa:
- **Jest**: Testing framework
- **@testing-library/react**: Para testing de componentes
- **@testing-library/jest-dom**: Matchers adicionales
- **babel-jest**: Transpilación JSX

### Archivos de Configuración
- `jest.config.js`: Configuración principal
- `babel.config.cjs`: Transpilación de código
- `src/setupTests.js`: Setup global y polyfills

## Troubleshooting

### Los tests no funcionan después de clonar
```bash
rm -rf node_modules package-lock.json
npm install
npm test
```

### Error: "jest-environment-jsdom not found"
```bash
npm install --save-dev jest-environment-jsdom
```

### Limpiar caché de Jest
```bash
npm test -- --clearCache
```

### Error con React hooks en tests
Los tests ya están configurados para manejar hooks correctamente. Si experimentas problemas:
1. Verifica que `src/setupTests.js` exista
2. Ejecuta: `npm test -- --clearCache`
3. Reinstala node_modules si persiste

## Desarrollo

```bash
# Dev server con HMR
npm run dev

# Build para producción
npm run build

# Preview del build
npm run preview

# Linting
npm run lint
```

## Contribuir

1. Crea una rama: `git checkout -b feature/tu-feature`
2. Commit cambios: `git commit -m "Tu mensaje"`
3. Push: `git push origin feature/tu-feature`
4. Abre Pull Request

**Importante:** Antes de push, asegúrate que los tests pasen:
```bash
npm test -- --watchAll=false
```

## License

MIT
