# shopping_cart.py

from typing import List, Dict

class Cart:
    def __init__(self):
        # Cada item tendrá: game_id, articulo (título), precio
        self.articulos: List[Dict] = []

    def agregar_articulo(self, game_id: str, articulo: str, precio: float) -> bool:
        """
        Devuelve True si se agregó un nuevo juego.
        Devuelve False si el juego ya estaba en el carrito.
        """
        for item in self.articulos:
            if item["game_id"] == game_id:
                return False  # ya existe

        self.articulos.append({
            "game_id": game_id,
            "articulo": articulo,
            "precio": precio
        })
        return True

    def remover_articulo(self, game_id: str) -> bool:
        """
        Elimina por game_id. Devuelve True si lo encontró y eliminó,
        False si no estaba en el carrito.
        """
        for item in list(self.articulos):
            if item["game_id"] == game_id:
                self.articulos.remove(item)
                return True
        return False

    def calcular_total(self) -> float:
        return sum(item["precio"] for item in self.articulos)

    def limpiar_carrito(self) -> None:
        self.articulos.clear()


# Instancia global de carrito, usada por main.py
cart = Cart()
