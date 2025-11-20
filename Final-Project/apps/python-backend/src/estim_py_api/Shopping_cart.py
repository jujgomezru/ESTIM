from fastapi import FastAPI, HTTPException
from pydantic import BaseModel

app = FastAPI()

class Cart:
    def __init__(self):
        self.articulos = []

    def agregar_articulo(self, articulo: str, precio: float):
        self.articulos.append({"articulo": articulo, "precio": precio})

    def remover_articulo(self, articulo: str) -> bool:
        for item in list(self.articulos):
            if item["articulo"] == articulo:
                self.articulos.remove(item)
                return True
        return False

    def calcular_total(self) -> float:
        return sum(item["precio"] for item in self.articulos)

cart = Cart()

class Item(BaseModel):
    articulo: str
    precio: float

@app.post("/shopping_cart/items")
async def add_item(item: Item):
    cart.agregar_articulo(item.articulo, item.precio)
    return {"message": "item agregado", "item": item.dict()}

@app.delete("/shopping_cart/items/{articulo}")
async def delete_item(articulo: str):
    if not cart.remover_articulo(articulo):
        raise HTTPException(status_code=404, detail="Artículo no encontrado")
    return {"message": "artículo eliminado"}

@app.get("/shopping_cart")
async def get_cart():
    return {"articulos": cart.articulos}

@app.get("/shopping_cart/total")
async def get_total():
    return {"total": cart.calcular_total()}