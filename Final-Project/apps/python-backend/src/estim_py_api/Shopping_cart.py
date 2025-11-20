from fastapi import FastAPI

app = FastAPI()

class Cart:
    def __init__(self):
        self.articulos = []
    
    def agregar_articulo(self, articulo, precio):
        self.articulos.append({"articulo" : articulo, 
                               "precio" : precio})
    
    def remover_articulo(self, articulo):
        for item in self.articulos:
            if item["articulo"] == articulo:
                self.articulos.remove(item)
                break
    
    def calcular_total(self):
        total = 0
        for item in self.articulos:
            total += item["precio"]
        return total

@app.post("/shooping_cart")
async def shoppingcart():
    return Cart