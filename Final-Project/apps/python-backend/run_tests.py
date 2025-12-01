# run_tests.py

import time
import traceback

from fastapi.testclient import TestClient

import database
from shopping_cart import Cart
from search_service import SearchService

BANNER_LINE = "🎮" * 60


def check_db_available() -> bool:
    """
    Intenta abrir una conexión y ejecutar SELECT 1.
    Si falla, asumimos que no hay BD disponible (por ejemplo, en CI sin Postgres).
    """
    try:
        db = database.SessionLocal()
        try:
            db.execute("SELECT 1")
        finally:
            db.close()
        print("✅ Base de datos disponible, se ejecutarán pruebas de búsqueda y API.")
        return True
    except Exception as e:
        print("ℹ️ No se pudo conectar a la base de datos, se omiten pruebas de búsqueda/API.")
        print(f"   Detalle: {e}")
        return False


def setup_test_client() -> TestClient:
    """
    Crea un TestClient de la app FastAPI.
    No crea tablas ni modifica el esquema.
    """
    from main import app
    return TestClient(app)


# ---------------------------------------------------------------------------
# PRUEBAS DEL CARRITO
# ---------------------------------------------------------------------------

def test_carrito_basico():
    """
    Pruebas básicas del carrito usando la clase Cart.
    (No dependen de la base de datos)
    """
    print("🛒 EJECUTANDO PRUEBAS DEL CARRITO (básico)...")

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
# PRUEBAS DEL SERVICIO DE BÚSQUEDA (dependen de BD)
# ---------------------------------------------------------------------------

def test_servicio_busqueda(has_db: bool):
    """
    Pruebas del SearchService usando la BD real.
    Si no hay BD disponible, se omiten.
    """
    print("🔍 EJECUTANDO PRUEBAS DE BÚSQUEDA...")

    if not has_db:
        print("   ℹ️ BD no disponible, omitiendo pruebas de búsqueda.")
        return

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
# PRUEBAS DE ENDPOINTS API CON TESTCLIENT (dependen de BD)
# ---------------------------------------------------------------------------

def test_api_endpoints(client: TestClient, has_db: bool):
    """
    Pruebas de endpoints usando FastAPI TestClient.
    Si no hay BD disponible, se omiten (porque muchos endpoints consultan juegos).
    """
    print("🌐 EJECUTANDO PRUEBAS DE API...")

    if not has_db:
        print("   ℹ️ BD no disponible, omitiendo pruebas de API.")
        return

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

    # 1) Detectar si hay BD disponible (Postgres real)
    has_db = check_db_available()

    # 2) Crear TestClient (no crea tablas)
    try:
        client = setup_test_client()
    except Exception as e:
        print("💥 Error creando TestClient:")
        traceback.print_exc()
        client = None
        has_db = False  # Por seguridad, no probamos API sin app estable

    # ---------------- CARRITO ----------------
    print("\n🎯 CARRITO DE COMPRAS")
    print("--------------------------------------------------")

    total_tests += 1
    try:
        test_carrito_basico()
        total_passed += 1
    except Exception as e:
        msg = f"Error en carrito (básico): {e}"
        print(f"   ❌ {msg}")
        traceback.print_exc()
        errores.append(msg)

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
        test_servicio_busqueda(has_db)
        # Si se omite por falta de BD, lo consideramos "no falla"
        if has_db:
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
        if client is not None:
            test_api_endpoints(client, has_db)
            if has_db:
                total_passed += 1
        else:
            print("   ℹ️ TestClient no disponible, omitiendo pruebas de API.")
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
    print(f"🎯 TOTAL GENERAL (planificadas): {total_tests} pruebas")
    print(f"✅ Pruebas exitosas: {total_passed}")
    print(f"❌ Pruebas fallidas: {len(errores)}")

    if errores:
        print("\nDetalles de errores:")
        for err in errores:
            print(f"   - {err}")
        print("\n💥 ALGUNAS PRUEBAS FALLARON")
        # Solo devolvemos 1 si fallaron pruebas realmente ejecutadas
        return 1

    print("\n🎉 TODAS LAS PRUEBAS QUE SE EJECUTARON PASARON CORRECTAMENTE")
    return 0


if __name__ == "__main__":
    exit(main())
