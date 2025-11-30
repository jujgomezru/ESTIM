# DOCUMENTACIÓN PYTHON-BACKEND

## 🚀 Endpoints Implementados

### 🔍 Endpoints Básicos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/` | Health check básico |
| `GET` | `/health` | Estado del servicio |
| `GET` | `/test-db` | Verifica la conexión con la base de datos |

### 🛒 Carrito de Compras
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/shopping_cart/items/{game_id}` | Agregar al carrito |
| `DELETE` | `/shopping_cart/items/{game_id}` | Eliminar artículo del carrito |
| `GET` | `/shopping_cart` | Consultar carrito |
| `GET` | `/shopping_cart/total` | Calcular total |
| `DELETE` | `/shopping_cart/clear` | Eliminar todo el carrito |

### 🎮 Catálogo de Juegos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `GET` | `/games/` | Lista de juegos disponibles |
| `GET` | `/games/search` | Busca por nombre, precio mínimo y máximo |
| `GET` | `/games/search/genre` | Busca por género |
| `GET` | `/games/popular` | Juegos más populares actualmente |
| `GET` | `/games/recent` | Búsquedas recientes |

### ⚙️ Administración
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/admin/seed-data` | Insertar datos de prueba |

-------------------------------

## 🔄 FLUJO DE DATOS
Cliente → FastAPI Endpoint
⬇️
Endpoint → Dependency Injection (BD Session)
⬇️
Business Logic → Cart Operations
⬇️
Data Access → SQLAlchemy Query
⬇️
Response → Pydantic Model → JSON
--------------------------------

## 📐 PRINCIPIOS SOLID APLICADOS

### 1. ✅ **Principio de Responsabilidad Única (SRP)**
Cada módulo tiene una única razón para cambiar:

- **`main.py`**: Responsable exclusivamente de definir los endpoints de la API y manejar las rutas HTTP
- **`Shopping_cart.py`**: Responsable únicamente de la lógica de negocio del carrito de compras
- **`database.py`**: Responsable exclusivamente de la conexión a base de datos y definición de modelos
- **`seed_data.py`**: Responsable únicamente de la inserción de datos de prueba

### 2. ✅ **Principio Abierto/Cerrado (OCP)**
Las entidades deben estar abiertas para extensión pero cerradas para modificación:

- El sistema está diseñado para extender funcionalidades sin modificar código existente
- Podemos agregar nuevos tipos de items al carrito (DLCs, paquetes, suscripciones) sin cambiar la lógica base
- Podemos añadir nuevos endpoints sin afectar los existentes
- La estructura de modelos permite agregar nuevos campos sin romper funcionalidad existente

### 3. ✅ **Principio de Sustitución de Liskov (LSP)**
Los objetos deben ser reemplazables por instancias de sus subtipos sin alterar el comportamiento:

- Los modelos de respuesta Pydantic pueden usarse indistintamente donde se esperan datos de juegos
- El carrito maneja items de forma genérica, permitiendo futuros tipos de productos
- Las dependencias inyectadas (sesiones de BD) son intercambiables y consistentes
- Las respuestas de error mantienen una estructura uniforme en toda la API

### 4. ✅ **Principio de Segregación de Interfaces (ISP)**
Muchas interfaces específicas son mejores que una interfaz general:

- **Endpoints específicos para operaciones específicas**:
  - `GET /shopping_cart` solo para consultar
  - `POST /shopping_cart/items` solo para agregar
  - `DELETE /shopping_cart/items` solo para eliminar

- **Dependencias separadas para diferentes concerns**:
  - Dependencia de base de datos para operaciones CRUD
  - Dependencia de carrito para operaciones de negocio
  - Modelos de respuesta específicos para diferentes vistas de datos

### 5. ✅ **Principio de Inversión de Dependencias (DIP)**
Depender de abstracciones, no de implementaciones concretas:

- Los endpoints dependen de la abstracción Session de SQLAlchemy, no de una implementación específica de PostgreSQL
- La lógica de negocio depende de interfaces de carrito, no de implementaciones concretas de almacenamiento
- FastAPI inyecta dependencias a través de abstracciones, no de implementaciones directas
- El sistema podría cambiar de PostgreSQL a MySQL modificando solo la capa de datos, sin afectar el negocio

-------------------------------------

## 🏗️ ARQUITECTURA DEL SISTEMA
                CAPA DE PRESENTACIÓN
                (FastAPI Endpoints)
                        ⬇️
                 CAPA DE SERVICIOS
            (Lógica de Negocio - Carrito)
                        ⬇️
                CAPA DE ACCESO A DATOS
              (SQLAlchemy ORM + Models)
                        ⬇️
                  CAPA DE DATOS
               (PostgreSQL Database)

-------------------------------------

## 🧪 PRUEBAS UNITARIAS

Se creó el archivo `run_tests.py` donde se corren las pruebas de:
- ✅ Agregar artículo
- ✅ Remover artículo  
- ✅ Cálculo del total

**Con resultados positivos**

![alt text](image.png)
