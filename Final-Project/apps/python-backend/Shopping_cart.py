class Cart:
    def __init__(self):
        self.articulos = []

    def agregar_articulo(self, game_id: str, articulo: str, precio: float):
        # Verificar si ya está en el carrito
        for item in self.articulos:
            if item["game_id"] == game_id:
                return False
                
        self.articulos.append({
            "game_id": game_id,
            "articulo": articulo, 
            "precio": precio
        })
        return True

    def remover_articulo(self, game_id: str) -> bool:
        for item in list(self.articulos):
            if item["game_id"] == game_id:
                self.articulos.remove(item)
                return True
        return False

    def calcular_total(self) -> float:
        return sum(item["precio"] for item in self.articulos)

    def limpiar_carrito(self):
        self.articulos.clear()

cart = Cart()