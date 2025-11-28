#!/usr/bin/env python3
import sys
import os

# Agregar el directorio actual al path
current_dir = os.path.dirname(os.path.abspath(__file__))
sys.path.insert(0, current_dir)

from database import SessionLocal, GameDB

# Importar después de agregar al path
try:
    from search_service import SearchService
    print("✅ SearchService importado correctamente")
except ImportError as e:
    print(f"❌ Error importando SearchService: {e}")
    # Intentar importación alternativa
    try:
        import search_service
        SearchService = search_service.SearchService
        print("✅ SearchService importado con método alternativo")
    except Exception as e2:
        print(f"❌ Error alternativo: {e2}")
        sys.exit(1)

def test_current_search():
    print("🧪 TESTEANDO EL CÓDIGO ACTUAL DE search_by_genre")
    print("=" * 50)
    
    db = SessionLocal()
    
    try:
        # Esto usará el código ACTUAL que está en memoria
        print("🔍 Ejecutando SearchService.search_by_genre(db, 'RPG')...")
        results = SearchService.search_by_genre(db, "RPG")
        print(f"✅ ÉXITO: {len(results)} juegos encontrados")
        
        for game in results:
            print(f"   🎮 {game['title']}")
            print(f"      Precio: ${game['price']}")
            print(f"      Rating: {game['average_rating']}")
            print(f"      Géneros: {game.get('genres', [])}")
            print()
            
    except Exception as e:
        print(f"❌ ERROR: {e}")
        import traceback
        print("📋 TRACEBACK COMPLETO:")
        traceback.print_exc()
    finally:
        db.close()

if __name__ == "__main__":
    test_current_search()