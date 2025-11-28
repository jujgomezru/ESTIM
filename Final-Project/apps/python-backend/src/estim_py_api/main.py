from fastapi import FastAPI, HTTPException, Depends
from sqlalchemy.orm import Session
import uuid
from .database import get_db, GameDB
from .Shopping_cart import cart
from .database import get_db, GameDB, create_tables

app = FastAPI()

@app.on_event("startup")
def startup_event():
    create_tables()
    print("Database tables ready")

# Endpoints básicos
@app.get("/")
async def root():
    return {"message": "ESTIM API funcionando"}

@app.get("/health")
async def health():
    return {"status": "healthy"}

# Endpoint para probar base de datos
@app.get("/test-db")
async def test_db(db: Session = Depends(get_db)):
    try:
        count = db.query(GameDB).count()
        return {"database": "connected", "total_games": count}
    except Exception as e:
        return {"database": "error", "error": str(e)}

# Carrito de compras
@app.post("/shopping_cart/items/{game_id}")
async def add_item(game_id: str, db: Session = Depends(get_db)):
    try:
        game_uuid = uuid.UUID(game_id)
    except ValueError:
        raise HTTPException(status_code=400, detail="ID de juego inválido")
    
    game = db.query(GameDB).filter(GameDB.id == game_uuid, GameDB.is_published == True).first()
    
    if not game:
        raise HTTPException(status_code=404, detail="Juego no encontrado o no publicado")
    
    if cart.agregar_articulo(str(game.id), game.title, float(game.price)):
        return {
            "message": "Juego agregado al carrito", 
            "item": {
                "game_id": str(game.id),
                "articulo": game.title,
                "precio": float(game.price)
            }
        }
    else:
        raise HTTPException(status_code=400, detail="El juego ya está en el carrito")

@app.delete("/shopping_cart/items/{game_id}")
async def delete_item(game_id: str):
    if not cart.remover_articulo(game_id):
        raise HTTPException(status_code=404, detail="Juego no encontrado en el carrito")
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

# Juegos
@app.get("/games/")
async def get_games(db: Session = Depends(get_db)):
    games = db.query(GameDB).filter(GameDB.is_published == True).limit(20).all()
    return [
        {
            "id": str(game.id),
            "title": game.title,
            "price": float(game.price),
            "description": game.short_description,
            "average_rating": float(game.average_rating)
        }
        for game in games
    ]

# Datos de prueba (opcional)
@app.post("/admin/seed-data")
async def seed_sample_data(db: Session = Depends(get_db)):
    """Endpoint para insertar datos de prueba"""
    from .seed_data import create_sample_games
    try:
        create_sample_games(db)
        return {"message": "Datos de prueba insertados correctamente"}
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error: {str(e)}")