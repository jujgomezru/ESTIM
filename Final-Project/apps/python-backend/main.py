# main.py

from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy.orm import Session
from fastapi.middleware.cors import CORSMiddleware
import uuid

from database import get_db, GameDB
from shopping_cart import cart
from search_service import SearchService

app = FastAPI()

# ==================== CORS ====================

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:5173"],  # URL del frontend Vite
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


# ==================== STARTUP: SOLO COMPROBACIÓN, NO SCHEMA ====================

@app.on_event("startup")
def startup_event():
    """
    En producción:
      - NO creamos tablas.
      - Asumimos que el esquema ya fue creado por migraciones SQL.
      - Solo hacemos un sanity check de conexión.
    """
    try:
        db = next(get_db())
        db.execute("SELECT 1")
        print("✅ Conexión a BD OK (esquema asumido existente)")
    except Exception as e:
        print(f"❌ Error conectando a BD: {e}")


# ==================== ENDPOINTS BÁSICOS ====================

@app.get("/")
async def root():
    return {"message": "ESTIM API funcionando"}


@app.get("/health")
async def test_health():
    """Endpoint simple para verificar que la API funciona"""
    return {
        "status": "healthy",
        "services": {
            "api": "running",
            "search": "available",
            "cart": "available",
        },
    }


@app.get("/test-db")
async def test_db(db: Session = Depends(get_db)):
    """Probar que la BD responde y la tabla games existe."""
    try:
        count = db.query(GameDB).count()
        return {"database": "connected", "total_games": count}
    except Exception as e:
        return {"database": "error", "error": str(e)}


# ==================== CARRITO DE COMPRAS (IN-MEMORY) ====================

@app.post("/shopping_cart/items/{game_id}")
async def add_item(game_id: str, db: Session = Depends(get_db)):
    try:
        game_uuid = uuid.UUID(game_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="ID de juego inválido")

    game = (
        db.query(GameDB)
        .filter(GameDB.id == game_uuid, GameDB.is_published == True)
        .first()
    )

    if not game:
        raise HTTPException(
            status_code=404, detail="Juego no encontrado o no publicado"
        )

    if cart.agregar_articulo(str(game.id), game.title, float(game.price)):
        return {
            "message": "Juego agregado al carrito",
            "item": {
                "game_id": str(game.id),
                "articulo": game.title,
                "precio": float(game.price),
            },
        }
    else:
        raise HTTPException(
            status_code=400, detail="El juego ya está en el carrito"
        )


@app.delete("/shopping_cart/items/{game_id}")
async def delete_item(game_id: str):
    if not cart.remover_articulo(game_id):
        raise HTTPException(
            status_code=404, detail="Juego no encontrado en el carrito"
        )
    return {"message": "Juego eliminado del carrito"}


@app.get("/shopping_cart")
async def get_cart():
    return {"articulos": cart.articulos}


@app.get("/shopping_cart/total")
async def get_total():
    return {"total": cart.calcular_total()}


@app.delete("/shopping_cart/clear")
async def clear_cart():
    cart.limpiar_carrito()
    return {"message": "Carrito vaciado"}


# ==================== JUEGOS Y BÚSQUEDA ====================

@app.get("/games/")
async def get_games(db: Session = Depends(get_db)):
    """Obtener todos los juegos publicados"""
    try:
        games = (
            db.query(GameDB)
            .filter(GameDB.is_published == True)
            .limit(20)
            .all()
        )
        return [
            {
                "id": str(game.id),
                "title": game.title,
                "price": float(game.price),
                "description": game.short_description,
                "average_rating": float(game.average_rating),
            }
            for game in games
        ]
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/search/")
async def search_games(
    q: str = "",
    min_price: float | None = None,
    max_price: float | None = None,
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
):
    """Buscar juegos por título, descripción o características"""
    try:
        results = SearchService.search_games(
            db=db,
            search_term=q,
            min_price=min_price,
            max_price=max_price,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/search/genre/")
async def search_by_genre(
    genre: str,
    skip: int = 0,
    limit: int = 20,
    db: Session = Depends(get_db),
):
    """Buscar juegos por género"""
    try:
        results = SearchService.search_by_genre(
            db=db,
            genre=genre,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/popular/")
async def get_popular_games(
    skip: int = 0,
    limit: int = 10,
    db: Session = Depends(get_db),
):
    """Obtener juegos populares"""
    try:
        results = SearchService.get_popular_games(
            db=db,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/games/recent/")
async def get_recent_games(
    skip: int = 0,
    limit: int = 10,
    db: Session = Depends(get_db),
):
    """Obtener juegos recientemente lanzados"""
    try:
        results = SearchService.get_recent_games(
            db=db,
            skip=skip,
            limit=limit,
        )
        return results
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


