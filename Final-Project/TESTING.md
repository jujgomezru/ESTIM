# GAMESTORE - Instrucciones de Ejecución de Tests

## Para Desarrolladores que Clonan el Repositorio

Si acabas de clonar este proyecto y quieres ejecutar los tests de la aplicación web, sigue estos pasos:

### Paso 1: Navegar al directorio web
```bash
cd Final-Project/web
```

### Paso 2: Instalar dependencias
```bash
npm install
```
Esto instalará todas las dependencias incluyendo Jest y librerías de testing.

### Paso 3: Ejecutar los tests
```bash
npm test
```

### Paso 4: Resultado esperado
Deberías ver algo como esto:
```
 PASS  src/components/__test__/Button.test.jsx
 PASS  src/components/__test__/GameCard.test.jsx
 PASS  src/components/__test__/Header.test.jsx

Test Suites: 3 passed, 3 total
Tests:       15 passed, 15 total
Snapshots:   0 total
Time:        ~2s
```

## Información de Testing

### ¿Dónde están los tests?
Los tests están en: `src/components/__test__/`

### ¿Qué se prueba?
- **Button.test.jsx**: Tests del componente Button (5 tests)
- **GameCard.test.jsx**: Tests del componente GameCard (6 tests)
- **Header.test.jsx**: Tests del componente Header (4 tests)

### Ejecutar tests en modo watch (modo desarrollo)
```bash
npm test -- --watch
```
Los tests se re-ejecutarán automáticamente cuando cambies archivos.

### Ejecutar tests una sola vez (sin watch)
```bash
npm test -- --watchAll=false
```

## Requisitos

- Node.js v16 o superior
- npm o yarn
- React 19.x con @testing-library/react 16+

## Troubleshooting

Si tienes problemas:

1. **Asegúrate de estar en el directorio correcto**
   ```bash
   cd Final-Project/web
   pwd  # o "cd" en Windows para ver la ruta actual
   ```

2. **Reinstala node_modules**
   ```bash
   rm -rf node_modules package-lock.json
   npm install
   ```

3. **Limpia el caché de Jest**
   ```bash
   npm test -- --clearCache
   ```

4. **Verifica la versión de Node**
   ```bash
   node --version
   # Debe ser v16 o superior
   ```

## Documentación Adicional

Ver `README-SETUP.md` en el directorio `web/` para más detalles sobre la configuración del proyecto y comandos disponibles.
