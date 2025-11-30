# run_tests.py

import time
import traceback

from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker

from fastapi.testclient import TestClient

import database
from database import Base
from seed_data import create_sample_games
from shopping_cart import Cart
from search_service import SearchService


BANNER_LINE = "🎮" * 60


def setup_test_database():
    """
    Configura una BD de pruebas en SQLite en memoria y reemplaza
    engine y SessionLocal del módulo database.
    """
    print("🛠️  Configurando base de datos de pruebas (SQLite en memoria)...")

    test_engine = create_engine(
        "sqlite:///:memory:",
        connect_args={"check_same_thread": False},
    )
    TestingSessionLocal = sessionmaker(
        autocommit=False,
        autoflush=False,
        bind=test_engine,
    )

    # Sobrescribimos engine y SessionLocal en el módulo database
    database.engine = test_engine
    database.SessionLocal = TestingSessionLocal

    # Crear tablas en la BD de pruebas
    Base.metadata.create_all(bind=test_engine)

    # Sembrar datos de ejemplo
    db = TestingSessionLocal()
    try:
        create_sample_games(db)
    finally:
        db.close()

    print("✅ Base de datos de pruebas lista.")


def setup_test_client():
    """
    Devuelve un TestClient de FastAPI con la app configurada
    para usar la BD de pruebas.
    """
    from main import app  # Importamos después de configurar la BD

    client = TestClient(app)
    return client


# ---------------------------------------------------------------------------
# PRUEBAS DEL CARRITO
# ---------------------------------------------------------------------------

def test_carrito_basico():
    """
    Pruebas básicas del carrito usando la clase Cart.
    """
    print("🛒 EJECUTANDO PRUEBAS DEL CARRITO...")

    cart = Cart()

    # 1) Carrito vacío
    assert len(cart.articulos) == 0, "El carrito debería iniciar vacío"
    assert cart.calcular_total() == 0, "El total inicial debería ser 0"

    # 2) Agregar un juego
    added = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added is True, "Debería agregarse el juego la primera vez"
    assert len(cart.articulos) == 1, "El carrito debería tener 1 artículo"
    assert cart.calcular_total() == 10.0, "El total debería ser 10.0"

    # 3) No duplicar juegos por game_id
    added_again = cart.agregar_articulo("game-1", "Juego de prueba", 10.0)
    assert added_again is False, "No debería agregarse dos veces el mismo game_id"
    assert len(cart.articulos) == 1, "El carrito debería seguir con 1 artículo"

    # 4) Remover juego
    removed = cart.remover_articulo("game-1")
    assert removed is True, "Debería poder remover el juego"
    assert len(cart.articulos) == 0, "El carrito debería quedar vacío"
    assert cart.calcular_total() == 0, "El total debería volver a 0"

    print("   ✅ Pruebas básicas de carrito OK.")


def test_carrito_flujo_completo():
    """
    Flujo más largo del carrito (varios juegos).
    """
    print("🧪 EJECUTANDO PRUEBA DE FLUJO COMPLETO DEL CARRITO...")

    cart = Cart()

    cart.agregar_articulo("game-a", "Juego A", 15.0)
    cart.agregar_articulo("game-b", "Juego B", 20.0)
    cart.agregar_articulo("game-c", "Juego C", 5.0)

    assert len(cart.articulos) == 3, "Debería haber 3 juegos en el carrito"
    assert cart.calcular_total() == 40.0, "El total debería ser 40.0"

    cart.remover_articulo("game-b")
    assert len(cart.articulos) == 2, "Debería haber 2 juegos tras remover uno"
    assert cart.calcular_total() == 20.0, "El total debería ser 20.0"

    cart.limpiar_carrito()
    assert len(cart.articulos) == 0, "El carrito debería quedar vacío tras limpiar"
    print("   ✅ Flujo completo de carrito OK.")


# ---------------------------------------------------------------------------
# PRUEBAS DEL SERVICIO DE BÚSQUEDA
# ---------------------------------------------------------------------------

def test_servicio_busqueda():
    """
    Pruebas del SearchService usando la BD de pruebas.
    """
    print("🔍 EJECUTANDO PRUEBAS DE BÚSQUEDA...")

    db = database.SessionLocal()
    try:
        # Búsqueda general
        results = SearchService.search_games(db, search_term="RPG")
        assert isinstance(results, list), "search_games debería devolver una lista"
        print(f"   🔸 Resultados búsqueda 'RPG': {len(results)}")

        # Búsqueda por género
        results_genre = SearchService.search_by_genre(db, genre="Action")
        assert isinstance(results_genre, list), "search_by_genre debería devolver una lista"
        print(f"   🔸 Resultados género 'Action': {len(results_genre)}")

        # Populares
        results_popular = SearchService.get_popular_games(db)
        assert isinstance(results_popular, list), "get_popular_games debería devolver una lista"
        print(f"   🔸 Resultados populares: {len(results_popular)}")

        # Recientes
        results_recent = SearchService.get_recent_games(db)
        assert isinstance(results_recent, list), "get_recent_games debería devolver una lista"
        print(f"   🔸 Resultados recientes: {len(results_recent)}")

    finally:
        db.close()

    print("   ✅ Pruebas del servicio de búsqueda OK.")


# ---------------------------------------------------------------------------
# PRUEBAS DE ENDPOINTS API CON TESTCLIENT
# ---------------------------------------------------------------------------

def test_api_endpoints(client: TestClient):
    """
    Pruebas de endpoints usando FastAPI TestClient, sin servidor real.
    """
    print("🌐 EJECUTANDO PRUEBAS DE API...")

    endpoints = [
        ("/", "Endpoint raíz"),
        ("/health", "Health check"),
        ("/shopping_cart", "Carrito de compras"),
        ("/shopping_cart/total", "Total del carrito"),
        ("/games/", "Lista de juegos"),
        ("/games/search/", "Búsqueda de juegos"),
        ("/games/popular/", "Juegos populares"),
        ("/games/recent/", "Juegos recientes"),
    ]

    for path, label in endpoints:
        print(f"   🔸 Probando {path} ({label})...")
        resp = client.get(path)
        assert resp.status_code == 200, f"{label} - status {resp.status_code}"
        print(f"      ✅ {label} OK (status {resp.status_code})")

    print("   ✅ Todas las pruebas de API OK.")


# ---------------------------------------------------------------------------
# RUNNER PRINCIPAL
# ---------------------------------------------------------------------------

def main():
    print(BANNER_LINE)
    print("🎯 PRUEBAS COMPLETAS - SISTEMA ESTIM")
    print("🎮 Carrito + Búsqueda + API")
    print(BANNER_LINE)
    print("\n🚀 INICIANDO SUITE COMPLETA DE PRUEBAS...\n")

    total_tests = 0
    total_passed = 0
    errores = []

    start_time = time.time()

    # 1) Configurar BD de prueba y cliente de API
    try:
        setup_test_database()
        client = setup_test_client()
    except Exception as e:
        print("💥 Error configurando entorno de pruebas:")
        traceback.print_exc()
        return 1

    # ---------------- CARRITO ----------------
    print("\n🎯 CARRITO DE COMPRAS")
    print("--------------------------------------------------")

    # test_carrito_basico
    total_tests += 1
    try:
        test_carrito_basico()
        total_passed += 1
    except Exception as e:
        msg = f"Error en carrito (básico): {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    # test_carrito_flujo_completo
    total_tests += 1
    try:
        test_carrito_flujo_completo()
        total_passed += 1
    except Exception as e:
        msg = f"Error en carrito (flujo completo): {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    # ---------------- BÚSQUEDA ----------------
    print("\n🎯 SERVICIO DE BÚSQUEDA")
    print("--------------------------------------------------")
    total_tests += 1
    try:
        test_servicio_busqueda()
        total_passed += 1
    except Exception as e:
        msg = f"Error en servicio de búsqueda: {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    # ---------------- API ----------------
    print("\n🎯 ENDPOINTS API")
    print("--------------------------------------------------")
    total_tests += 1
    try:
        test_api_endpoints(client)
        total_passed += 1
    except Exception as e:
        msg = f"Error en endpoints API: {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

    # ---------------- RESUMEN ----------------
    end_time = time.time()
    elapsed = end_time - start_time

    print("\n" + "📊" * 60)
    print("📈 RESUMEN COMPLETO DE PRUEBAS")
    print("📊" * 60 + "\n")

    print(f"⏱️  Tiempo total de ejecución: {elapsed:.2f} segundos\n")
    print(f"🎯 TOTAL GENERAL: {total_tests} pruebas ejecutadas")
    print(f"✅ Pruebas exitosas: {total_passed}")
    print(f"❌ Pruebas fallidas: {len(errores)}")

    if errores:
        print("\nDetalles de errores:")
        for err in errores:
            print(f"   - {err}")
        print("\n💥 ALGUNAS PRUEBAS FALLARON")
        return 1

    print("\n🎉 TODAS LAS PRUEBAS PASARON CORRECTAMENTE")
    return 0


if __name__ == "__main__":
    exit(main())
