#!/usr/bin/env python3
"""
🎯 PRUEBAS UNITARIAS - SISTEMA DE CARRITO ESTIM
Script mejorado con mejor manejo de errores y imports
"""

import sys
import os
import time

# Configurar path de forma robusta
current_dir = os.path.dirname(os.path.abspath(__file__))
src_path = os.path.join(current_dir, 'src')
sys.path.insert(0, src_path)

print(f"📁 Directorio de trabajo: {current_dir}")
print(f"🔍 Buscando módulos en: {src_path}")

try:
    # Intentar importar el carrito
    from estim_py_api.Shopping_cart import Cart
    print("✅ Módulo Shopping_cart importado correctamente")
except ImportError as e:
    print(f"❌ Error importando Shopping_cart: {e}")
    print("\n🔎 Diagnosticando el problema...")
    
    # Mostrar estructura de archivos
    if os.path.exists(src_path):
        print("📂 Contenido de src/:")
        for item in os.listdir(src_path):
            item_path = os.path.join(src_path, item)
            if os.path.isdir(item_path):
                print(f"   📁 {item}/")
                for subitem in os.listdir(item_path):
                    print(f"      📄 {subitem}")
            else:
                print(f"   📄 {item}")
    else:
        print("❌ No existe el directorio src/")
    
    sys.exit(1)

class TestRunner:
    def __init__(self):
        self.tests_passed = 0
        self.tests_failed = 0
        self.errors = []
        self.start_time = None
        
    def print_banner(self):
        """Imprime un banner atractivo"""
        print("\n" + "⭐" * 60)
        print("⭐" + " " * 58 + "⭐")
        print("⭐              🎮 PRUEBAS UNITARIAS ESTIM 🎮               ⭐")
        print("⭐                 Sistema de Carrito de Compras            ⭐")
        print("⭐" + " " * 58 + "⭐")
        print("⭐" * 60)
        print()
    
    def run_test(self, test_name, test_function):
        """Ejecuta una prueba individual y maneja los resultados"""
        print(f"🔍 Ejecutando: {test_name}")
        
        try:
            result = test_function()
            if result:
                print(f"   ✅ {test_name} - EXITOSO")
                self.tests_passed += 1
                return True
            else:
                print(f"   ❌ {test_name} - FALLÓ (retornó False)")
                self.tests_failed += 1
                return False
                
        except AssertionError as e:
            print(f"   ❌ {test_name} - FALLÓ (AssertionError)")
            print(f"      Mensaje: {e}")
            self.tests_failed += 1
            self.errors.append(f"{test_name}: {e}")
            return False
            
        except Exception as e:
            print(f"   💥 {test_name} - ERROR inesperado")
            print(f"      Tipo: {type(e).__name__}")
            print(f"      Mensaje: {e}")
            self.tests_failed += 1
            self.errors.append(f"{test_name}: {type(e).__name__} - {e}")
            return False
    
    def test_agregar_articulo(self):
        """Prueba la inserción de artículos al carrito"""
        cart = Cart()
        
        # Test 1: Agregar artículo normal
        result1 = cart.agregar_articulo("game-001", "The Legend of Zelda", 59.99)
        assert result1 == True, "Debería poder agregar un artículo nuevo"
        assert len(cart.articulos) == 1, "Debería tener 1 artículo"
        assert cart.articulos[0]["game_id"] == "game-001", "El ID del juego debería coincidir"
        
        # Test 2: Agregar segundo artículo
        result2 = cart.agregar_articulo("game-002", "Mario Kart", 49.99)
        assert result2 == True, "Debería poder agregar un segundo artículo"
        assert len(cart.articulos) == 2, "Debería tener 2 artículos"
        
        # Test 3: No permitir duplicados
        result3 = cart.agregar_articulo("game-001", "The Legend of Zelda", 59.99)
        assert result3 == False, "No debería permitir agregar duplicados"
        assert len(cart.articulos) == 2, "No debería agregar el duplicado"
        
        return True
    
    def test_remover_articulo(self):
        """Prueba la eliminación de artículos del carrito"""
        cart = Cart()
        
        # Configurar datos de prueba
        cart.agregar_articulo("game-001", "The Legend of Zelda", 59.99)
        cart.agregar_articulo("game-002", "Mario Kart", 49.99)
        cart.agregar_articulo("game-003", "Animal Crossing", 54.99)
        
        # Test 1: Remover artículo existente
        initial_count = len(cart.articulos)
        result1 = cart.remover_articulo("game-002")
        assert result1 == True, "Debería poder remover artículo existente"
        assert len(cart.articulos) == initial_count - 1, "Debería reducir la cantidad de artículos"
        
        # Test 2: Remover artículo que no existe
        result2 = cart.remover_articulo("game-999")
        assert result2 == False, "No debería poder remover artículo inexistente"
        assert len(cart.articulos) == initial_count - 1, "No debería cambiar la cantidad"
        
        # Test 3: Remover otro artículo existente
        result3 = cart.remover_articulo("game-001")
        assert result3 == True, "Debería poder remover otro artículo"
        assert len(cart.articulos) == initial_count - 2, "Debería reducir más artículos"
        
        return True
    
    def test_calcular_total(self):
        """Prueba el cálculo del total del carrito"""
        cart = Cart()
        
        # Test 1: Carrito vacío
        total_vacio = cart.calcular_total()
        assert total_vacio == 0.0, "Carrito vacío debería totalizar 0.0"
        
        # Test 2: Carrito con un artículo
        cart.agregar_articulo("game-001", "Juego 1", 29.99)
        total_uno = cart.calcular_total()
        assert total_uno == 29.99, f"Debería totalizar 29.99, pero es {total_uno}"
        
        # Test 3: Carrito con múltiples artículos
        cart.agregar_articulo("game-002", "Juego 2", 39.99)
        cart.agregar_articulo("game-003", "Juego 3", 19.99)
        total_multiple = cart.calcular_total()
        expected_total = 29.99 + 39.99 + 19.99
        assert abs(total_multiple - expected_total) < 0.01, f"Debería totalizar {expected_total}, pero es {total_multiple}"
        
        return True
    
    def test_limpiar_carrito(self):
        """Prueba la funcionalidad de limpiar carrito"""
        cart = Cart()
        
        # Llenar el carrito
        cart.agregar_articulo("game-001", "Juego 1", 29.99)
        cart.agregar_articulo("game-002", "Juego 2", 39.99)
        cart.agregar_articulo("game-003", "Juego 3", 19.99)
        
        # Verificar que tiene artículos
        assert len(cart.articulos) == 3, "Debería tener 3 artículos antes de limpiar"
        assert cart.calcular_total() > 0, "Debería tener total mayor a 0 antes de limpiar"
        
        # Limpiar carrito
        cart.limpiar_carrito()
        
        # Verificar que está vacío
        assert len(cart.articulos) == 0, "Debería estar vacío después de limpiar"
        assert cart.calcular_total() == 0.0, "Total debería ser 0.0 después de limpiar"
        
        return True
    
    def test_flujo_completo(self):
        """Prueba de integración completa del flujo del carrito"""
        cart = Cart()
        
        print("      🧪 Simulando flujo completo de usuario...")
        
        # Paso 1: Usuario agrega juegos al carrito
        cart.agregar_articulo("game-001", "Cyberpunk 2077", 49.99)
        cart.agregar_articulo("game-002", "The Witcher 3", 29.99)
        cart.agregar_articulo("game-003", "GTA V", 39.99)
        
        # Verificar estado intermedio
        assert len(cart.articulos) == 3, "Debería tener 3 juegos en el carrito"
        total_parcial = cart.calcular_total()
        expected_parcial = 49.99 + 29.99 + 39.99
        assert abs(total_parcial - expected_parcial) < 0.01, f"Total parcial incorrecto"
        
        # Paso 2: Usuario elimina un juego
        cart.remover_articulo("game-002")
        assert len(cart.articulos) == 2, "Debería tener 2 juegos después de eliminar uno"
        
        # Paso 3: Usuario agrega otro juego diferente
        cart.agregar_articulo("game-004", "Red Dead Redemption 2", 59.99)
        assert len(cart.articulos) == 3, "Debería tener 3 juegos después de agregar uno nuevo"
        
        # Paso 4: Verificar total final
        total_final = cart.calcular_total()
        expected_final = 49.99 + 39.99 + 59.99
        assert abs(total_final - expected_final) < 0.01, f"Total final incorrecto"
        
        # Paso 5: Usuario limpia todo el carrito
        cart.limpiar_carrito()
        assert len(cart.articulos) == 0, "Debería estar vacío al final"
        assert cart.calcular_total() == 0.0, "Total debería ser 0 al final"
        
        print("      ✅ Flujo completo ejecutado correctamente")
        return True
    
    def run_all_tests(self):
        """Ejecuta todas las pruebas"""
        self.start_time = time.time()
        self.print_banner()
        
        print("🚀 Iniciando suite de pruebas...\n")
        
        # Lista de todas las pruebas a ejecutar
        tests = [
            ("Inserción de Artículos", self.test_agregar_articulo),
            ("Eliminación de Artículos", self.test_remover_articulo),
            ("Cálculo de Total", self.test_calcular_total),
            ("Limpieza de Carrito", self.test_limpiar_carrito),
            ("Flujo Completo", self.test_flujo_completo)
        ]
        
        # Ejecutar cada prueba
        for test_name, test_function in tests:
            self.run_test(test_name, test_function)
            print()  # Línea en blanco entre pruebas
        
        self.show_results()
        
        return self.tests_failed == 0
    
    def show_results(self):
        """Muestra los resultados finales de las pruebas"""
        duration = time.time() - self.start_time
        total_tests = self.tests_passed + self.tests_failed
        
        print("\n" + "📊" * 60)
        print("📊                     RESUMEN DE RESULTADOS                     📊")
        print("📊" * 60)
        
        print(f"\n⏱️  Tiempo total de ejecución: {duration:.2f} segundos")
        print(f"🧪 Total de pruebas ejecutadas: {total_tests}")
        print(f"✅ Pruebas exitosas: {self.tests_passed}")
        print(f"❌ Pruebas fallidas: {self.tests_failed}")
        
        # Calcular porcentaje de éxito
        if total_tests > 0:
            success_rate = (self.tests_passed / total_tests) * 100
            print(f"📈 Tasa de éxito: {success_rate:.1f}%")
        
        # Mostrar errores si los hay
        if self.errors:
            print(f"\n⚠️  Errores detectados:")
            for error in self.errors:
                print(f"   • {error}")
        
        # Resultado final
        print("\n" + "🎯" * 60)
        if self.tests_failed == 0:
            print("🎉 ¡TODAS LAS PRUEBAS PASARON EXITOSAMENTE! 🎉")
            print("🚀 El sistema de carrito está funcionando PERFECTAMENTE")
            print("💪 El código es confiable y listo para producción")
        else:
            print("💥 ALGUNAS PRUEBAS FALLARON")
            print("🔧 Revisa los errores arriba y corrige el código")
        print("🎯" * 60)

def main():
    """Función principal"""
    try:
        runner = TestRunner()
        success = runner.run_all_tests()
        return success
    except KeyboardInterrupt:
        print("\n\n⏹️  Pruebas interrumpidas por el usuario")
        return False
    except Exception as e:
        print(f"\n💥 ERROR CRÍTICO: {e}")
        return False

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)