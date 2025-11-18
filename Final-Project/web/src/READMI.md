# Proyecto Steam - Frontend React

## Requisitos
- Node.js instalado
- Editor de código 
- Navegador 

---

## Instalación

```bash
# Crear proyecto
npm create vite@latest steam -- --template react


#instalaciones necesarias para correrlo
npm install
npm install react-router-dom

# Arrancar el servidor de desarrollo
npm run dev
```

---

## Estructura del proyecto

```
src/
├─ app/
│  ├─ App.jsx            ← inicia la app
│  ├─ main.jsx           ← monta App en el DOM
│  └─ routes.jsx         ← rutas de la app
├─ api/
│  ├─ apiClient.js       ← función para llamar al backend
│  └─ endpoints.js       ← URLs del backend
├─ components/           ← componentes simples reutilizables
│  ├─ Button.jsx
├─ hooks/
│  └─ useFetch.js        ← hook para pedir datos
├─ features/
│  ├─ auth/              ← login/registro
│  │  ├─ LoginPage.jsx
│  │  ├─ RegisterPage.jsx
│  │  └─ authService.js
│  └─ .../             
│     ├─ ....jsx
│     └─ ....js
└─ styles/
   └─ globals.css        ← estilos globales
```

---

## Flujo general del sistema

```
src/app/main.jsx
        ↓   (inicia la app)
src/app/App.jsx
        ↓   (carga rutas)
src/app/routes.jsx
        ↓   (muestra la página según URL)
src/features/.../Page.jsx
        ↓   (muestra UI y llama al service)
src/features/.../service.js
        ↓   (prepara petición)
src/api/apiClient.js  +  src/api/endpoints.js
        ↓   (hace fetch usando rutas)
BACKEND JAVA < localhost:8080 >
        ↓
Respuesta
        ↓
src/features/.../Page.jsx
            (actualiza la UI)
```

---

## Ejemplo: flujo de RegisterPage

```
src/app/main.jsx
        ↓   (inicia la app)
src/app/App.jsx
        ↓   (carga rutas)
src/app/routes.jsx
        ↓   (muestra RegisterPage según URL /register)
src/features/auth/RegisterPage.jsx
        ↓   (muestra formulario y llama a authService)
src/features/auth/authService.js
        ↓   (prepara la petición de registro)
src/api/apiClient.js  +  src/api/endpoints.js
        ↓   (hace fetch a /auth/register)
BACKEND JAVA < localhost:8080 >
        ↓   (procesa el registro y responde JSON)
Respuesta
        ↓
src/features/auth/RegisterPage.jsx
            (muestra mensaje de éxito o error)
```

---

## Notas
- Cada "feature" tiene su Page.jsx y service.js.
- La comunicación con backend **siempre** pasa por `apiClient.js`.
- `endpoints.js` centraliza todas las rutas para no repetir URLs.
- Este flujo sirve para **todas las funcionalidades**.

