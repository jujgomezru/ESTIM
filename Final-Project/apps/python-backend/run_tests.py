#!/usr/bin/env python3
"""
🎯 EJECUTOR DE PRUEBAS COMPLETO - ESTIM Backend
Incluye: Carrito de Compras + Servicio de Búsqueda + Endpoints API
"""

import sys
import os
import time

# Configurar path
current_dir = os.path.dirname(os.path.abspath(__file__))
src_path = os.path.join(current_dir, 'src')
sys.path.insert(0, src_path)

class TestRunner:
    def __init__(self):
        self.results = {
            'carrito': {'passed': 0, 'failed': 0, 'tests': []},
            'busqueda': {'passed': 0, 'failed': 0, 'tests': []},
            'api': {'passed': 0, 'failed': 0, 'tests': []}
        }
        self.start_time = None
    
    def print_header(self):
        """Imprime el encabezado de las pruebas"""
        print("🎮" * 60)
        print("🎯 PRUEBAS COMPLETAS - SISTEMA ESTIM")
        print("🎮 Carrito + Búsqueda + API")
        print("🎮" * 60)
        print()
    
    def test_carrito_basico(self):
        """PRUEBAS DEL CARRITO DE COMPRAS - Todas las operaciones"""
        print("🛒 EJECUTANDO PRUEBAS DEL CARRITO...")
        
        try:
            from estim_py_api.Shopping_cart import Cart
            
            # Crear instancia de carrito
            cart = Cart()
            
            # 🔹 PRUEBA 1: Carrito vacío
            print("   🔸 Probando carrito vacío...")
            assert len(cart.articulos) == 0, "Carrito debería empezar vacío"
            assert cart.calcular_total() == 0.0, "Total debería ser 0"
            self.results['carrito']['tests'].append("✅ Carrito vacío - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 2: Agregar artículo
            print("   🔸 Probando agregar artículo...")
            result = cart.agregar_articulo("test-1", "Juego Test", 29.99)
            assert result == True, "Debería poder agregar artículo"
            assert len(cart.articulos) == 1, "Debería tener 1 artículo"
            assert cart.articulos[0]["game_id"] == "test-1", "ID debería coincidir"
            self.results['carrito']['tests'].append("✅ Agregar artículo - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 3: Calcular total
            print("   🔸 Probando cálculo de total...")
            total = cart.calcular_total()
            assert total == 29.99, f"Total debería ser 29.99, es {total}"
            self.results['carrito']['tests'].append("✅ Calcular total - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 4: Prevenir duplicados
            print("   🔸 Probando prevención de duplicados...")
            result = cart.agregar_articulo("test-1", "Juego Test", 29.99)
            assert result == False, "No debería permitir duplicados"
            assert len(cart.articulos) == 1, "No debería agregar duplicado"
            self.results['carrito']['tests'].append("✅ Prevenir duplicados - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 5: Agregar segundo artículo
            print("   🔸 Probando agregar segundo artículo...")
            result = cart.agregar_articulo("test-2", "Juego Test 2", 39.99)
            assert result == True, "Debería poder agregar segundo artículo"
            assert len(cart.articulos) == 2, "Debería tener 2 artículos"
            total = cart.calcular_total()
            assert total == 69.98, f"Total debería ser 69.98, es {total}"
            self.results['carrito']['tests'].append("✅ Agregar segundo artículo - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 6: Eliminar artículo
            print("   🔸 Probando eliminar artículo...")
            result = cart.remover_articulo("test-1")
            assert result == True, "Debería poder eliminar artículo"
            assert len(cart.articulos) == 1, "Debería quedar 1 artículo"
            total = cart.calcular_total()
            assert total == 39.99, f"Total debería ser 39.99, es {total}"
            self.results['carrito']['tests'].append("✅ Eliminar artículo - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 7: Eliminar artículo inexistente
            print("   🔸 Probando eliminar artículo inexistente...")
            result = cart.remover_articulo("no-existe")
            assert result == False, "No debería poder eliminar artículo inexistente"
            assert len(cart.articulos) == 1, "No debería cambiar la cantidad"
            self.results['carrito']['tests'].append("✅ Eliminar artículo inexistente - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 8: Limpiar carrito
            print("   🔸 Probando limpiar carrito...")
            cart.limpiar_carrito()
            assert len(cart.articulos) == 0, "Debería estar vacío después de limpiar"
            assert cart.calcular_total() == 0.0, "Total debería ser 0 después de limpiar"
            self.results['carrito']['tests'].append("✅ Limpiar carrito - PASÓ")
            self.results['carrito']['passed'] += 1
            
            # 🔹 PRUEBA 9: Múltiples operaciones
            print("   🔸 Probando operaciones múltiples...")
            cart.agregar_articulo("game-1", "Juego 1", 10.0)
            cart.agregar_articulo("game-2", "Juego 2", 20.0)
            cart.agregar_articulo("game-3", "Juego 3", 30.0)
            assert len(cart.articulos) == 3, "Debería tener 3 artículos"
            assert cart.calcular_total() == 60.0, "Total debería ser 60.0"
            
            cart.remover_articulo("game-2")
            assert len(cart.articulos) == 2, "Debería tener 2 artículos después de eliminar"
            assert cart.calcular_total() == 40.0, "Total debería ser 40.0"
            
            cart.limpiar_carrito()
            assert len(cart.articulos) == 0, "Debería estar vacío al final"
            self.results['carrito']['tests'].append("✅ Operaciones múltiples - PASÓ")
            self.results['carrito']['passed'] += 1
            
            print("   ✅ CARRITO - TODAS LAS PRUEBAS PASARON")
            return True
            
        except Exception as e:
            error_msg = f"❌ Error en carrito: {e}"
            self.results['carrito']['tests'].append(error_msg)
            self.results['carrito']['failed'] += 1
            print(f"   {error_msg}")
            import traceback
            print(f"   Traceback: {traceback.format_exc()}")
            return False
    
    def test_servicio_busqueda(self):
        """PRUEBAS DEL SERVICIO DE BÚSQUEDA"""
        print("🔍 EJECUTANDO PRUEBAS DE BÚSQUEDA...")
        
        try:
            from estim_py_api.search_service import SearchService
            
            print("   🔸 Verificando importación...")
            # Verificar que la clase existe
            assert SearchService is not None, "SearchService no debería ser None"
            
            self.results['busqueda']['tests'].append("✅ Servicio de búsqueda - IMPORTADO")
            self.results['busqueda']['passed'] += 1
            
            print("   🔸 Verificando métodos...")
            # Verificar que los métodos existen
            methods = [method for method in dir(SearchService) if not method.startswith('_')]
            expected_methods = ['search_games', 'advanced_search', 'search_by_genre', 'get_popular_games', 'get_recent_games']
            
            for method in expected_methods:
                if method not in methods:
                    raise Exception(f"Método {method} no encontrado. Métodos disponibles: {methods}")
            
            self.results['busqueda']['tests'].append("✅ Métodos del servicio - ENCONTRADOS")
            self.results['busqueda']['passed'] += 1
            
            print("   🔸 Probando funciones de ayuda...")
            # Probar _safe_float con casos edge
            test_cases = [
                (None, None),
                ("29.99", 29.99),
                (30, 30.0),
                ("", None),
                ("invalid", None)
            ]
            
            for input_val, expected in test_cases:
                result = SearchService._safe_float(input_val)
                if result != expected:
                    raise Exception(f"_safe_float({input_val}) retornó {result}, esperado {expected}")
            
            self.results['busqueda']['tests'].append("✅ Funciones de ayuda - FUNCIONANDO")
            self.results['busqueda']['passed'] += 1
            
            print("   🔸 Verificando callability...")
            # Verificar que los métodos son callables
            assert callable(SearchService.search_games), "search_games debería ser callable"
            assert callable(SearchService.advanced_search), "advanced_search debería ser callable"
            assert callable(SearchService.search_by_genre), "search_by_genre debería ser callable"
            
            self.results['busqueda']['tests'].append("✅ Métodos - CALLABLES")
            self.results['busqueda']['passed'] += 1
            
            print("   ✅ BÚSQUEDA - CONFIGURACIÓN CORRECTA")
            return True
            
        except Exception as e:
            error_msg = f"❌ Error en servicio de búsqueda: {e}"
            self.results['busqueda']['tests'].append(error_msg)
            self.results['busqueda']['failed'] += 1
            print(f"   {error_msg}")
            import traceback
            print(f"   Traceback: {traceback.format_exc()}")
            return False
    
    def test_endpoints_api(self):
        """PRUEBAS DE LOS ENDPOINTS DE LA API"""
        print("🌐 EJECUTANDO PRUEBAS DE API...")
        
        try:
            import requests
            
            base_url = "http://localhost:8000"
            
            # Lista de endpoints a probar
            endpoints = [
                ("/", "Endpoint raíz"),
                ("/health", "Health check"), 
                ("/shopping_cart", "Carrito de compras"),
                ("/shopping_cart/total", "Total del carrito"),
                ("/games/", "Lista de juegos"),
                ("/games/search/", "Búsqueda de juegos"),
                ("/games/popular/", "Juegos populares"),
                ("/games/recent/", "Juegos recientes")
            ]
            
            successful_tests = 0
            
            for endpoint, description in endpoints:
                try:
                    print(f"   🔸 Probando {endpoint}...")
                    response = requests.get(f"{base_url}{endpoint}", timeout=10)
                    
                    if response.status_code == 200:
                        self.results['api']['tests'].append(f"✅ {endpoint} - RESPONDE (200)")
                        self.results['api']['passed'] += 1
                        successful_tests += 1
                        print(f"      ✅ {description} - OK")
                    else:
                        self.results['api']['tests'].append(f"⚠️  {endpoint} - CÓDIGO {response.status_code}")
                        self.results['api']['failed'] += 1
                        print(f"      ⚠️  {description} - Código {response.status_code}")
                        
                except requests.exceptions.RequestException as e:
                    self.results['api']['tests'].append(f"❌ {endpoint} - NO ACCESIBLE: {e}")
                    self.results['api']['failed'] += 1
                    print(f"      ❌ {description} - No accesible")
            
            # Verificar que al menos la mayoría de endpoints funcionan
            if successful_tests >= 5:
                print("   ✅ API - LA MAYORÍA DE ENDPOINTS FUNCIONAN")
                return True
            else:
                print("   ⚠️  API - MUCHOS ENDPOINTS NO RESPONDEN")
                return False
                
        except ImportError:
            self.results['api']['tests'].append("ℹ️  Módulo 'requests' no instalado")
            print("   ℹ️  API - Pruebas omitidas (falta 'requests')")
            return True
        except Exception as e:
            error_msg = f"❌ Error probando endpoints: {e}"
            self.results['api']['tests'].append(error_msg)
            self.results['api']['failed'] += 1
            print(f"   {error_msg}")
            return False
    
    def test_flujo_completo(self):
        """PRUEBA DE FLUJO COMPLETO DEL SISTEMA"""
        print("🔄 EJECUTANDO PRUEBA DE FLUJO COMPLETO...")
        
        try:
            from estim_py_api.Shopping_cart import Cart
            
            # Simular flujo completo de un usuario
            cart = Cart()
            
            print("   🔸 Flujo: Carrito vacío...")
            # 1. Usuario ve carrito vacío
            assert len(cart.articulos) == 0, "Carrito debería empezar vacío"
            
            print("   🔸 Flujo: Agregar juegos...")
            # 2. Usuario agrega juegos al carrito
            cart.agregar_articulo("game-1", "The Legend of Zelda", 59.99)
            cart.agregar_articulo("game-2", "Mario Kart", 49.99)
            cart.agregar_articulo("game-3", "Animal Crossing", 54.99)
            
            # 3. Verificar estado
            assert len(cart.articulos) == 3, "Debería tener 3 juegos"
            total = cart.calcular_total()
            expected_total = 59.99 + 49.99 + 54.99
            assert abs(total - expected_total) < 0.01, f"Total debería ser {expected_total}, es {total}"
            
            print("   🔸 Flujo: Eliminar juego...")
            # 4. Usuario elimina un juego
            cart.remover_articulo("game-2")
            assert len(cart.articulos) == 2, "Debería tener 2 juegos después de eliminar"
            
            print("   🔸 Flujo: Agregar juego diferente...")
            # 5. Usuario agrega otro juego
            cart.agregar_articulo("game-4", "Cyberpunk 2077", 39.99)
            assert len(cart.articulos) == 3, "Debería tener 3 juegos"
            
            print("   🔸 Flujo: Verificar total final...")
            # 6. Verificar total final
            total_final = cart.calcular_total()
            expected_final = 59.99 + 54.99 + 39.99
            assert abs(total_final - expected_final) < 0.01, f"Total final debería ser {expected_final}, es {total_final}"
            
            print("   🔸 Flujo: Limpiar carrito...")
            # 7. Usuario limpia el carrito
            cart.limpiar_carrito()
            assert len(cart.articulos) == 0, "Debería estar vacío al final"
            assert cart.calcular_total() == 0.0, "Total debería ser 0 al final"
            
            self.results['carrito']['tests'].append("✅ Flujo completo del sistema - PASÓ")
            self.results['carrito']['passed'] += 1
            
            print("   ✅ FLUJO COMPLETO - SIMULACIÓN EXITOSA")
            return True
            
        except Exception as e:
            error_msg = f"❌ Error en flujo completo: {e}"
            self.results['carrito']['tests'].append(error_msg)
            self.results['carrito']['failed'] += 1
            print(f"   {error_msg}")
            return False
    
    def run_all_tests(self):
        """Ejecuta todas las pruebas"""
        self.start_time = time.time()
        self.print_header()
        
        print("🚀 INICIANDO SUITE COMPLETA DE PRUEBAS...\n")
        
        # Ejecutar todas las pruebas
        tests = [
            ("CARRITO DE COMPRAS", self.test_carrito_basico),
            ("SERVICIO DE BÚSQUEDA", self.test_servicio_busqueda), 
            ("ENDPOINTS API", self.test_endpoints_api),
            ("FLUJO COMPLETO", self.test_flujo_completo)
        ]
        
        for test_name, test_func in tests:
            print(f"🎯 {test_name}")
            print("-" * 50)
            test_func()
            print()  # Línea en blanco entre tests
        
        self.show_results()
        
        return self.calculate_success()
    
    def calculate_success(self):
        """Calcula si las pruebas fueron exitosas en general"""
        total_passed = (self.results['carrito']['passed'] + 
                       self.results['busqueda']['passed'] + 
                       self.results['api']['passed'])
        
        total_failed = (self.results['carrito']['failed'] + 
                       self.results['busqueda']['failed'] + 
                       self.results['api']['failed'])
        
        return total_failed == 0
    
    def show_results(self):
        """Muestra los resultados detallados"""
        duration = time.time() - self.start_time
        
        print("📊" * 60)
        print("📈 RESUMEN COMPLETO DE PRUEBAS")
        print("📊" * 60)
        
        print(f"\n⏱️  Tiempo total de ejecución: {duration:.2f} segundos")
        
        # Mostrar resultados por categoría
        for category, data in self.results.items():
            total_tests = data['passed'] + data['failed']
            if total_tests > 0:
                success_rate = (data['passed'] / total_tests) * 100
            else:
                success_rate = 0
                
            print(f"\n🔹 {category.upper()}:")
            print(f"   Pruebas: {total_tests} | ✅ {data['passed']} | ❌ {data['failed']} | 📈 {success_rate:.1f}%")
            for test in data['tests']:
                print(f"   {test}")
        
        # Totales generales
        total_passed = sum(data['passed'] for data in self.results.values())
        total_failed = sum(data['failed'] for data in self.results.values())
        total_tests = total_passed + total_failed
        
        if total_tests > 0:
            overall_success_rate = (total_passed / total_tests) * 100
        else:
            overall_success_rate = 0
        
        print(f"\n🎯 TOTAL GENERAL: {total_tests} pruebas ejecutadas")
        print(f"✅ Pruebas exitosas: {total_passed}")
        print(f"❌ Pruebas fallidas: {total_failed}")
        print(f"📈 Tasa de éxito general: {overall_success_rate:.1f}%")
        
        # Resultado final
        print("\n" + "🎮" * 60)
        if total_failed == 0:
            print("🎉 ¡TODAS LAS PRUEBAS PASARON EXITOSAMENTE! 🎉")
            print("🚀 El sistema ESTIM está funcionando PERFECTAMENTE")
            print("💪 Carrito + Búsqueda + API - TODO LISTO PARA PRODUCCIÓN")
        else:
            print("💥 ALGUNAS PRUEBAS FALLARON")
            print("🔧 Revisa los detalles arriba para corregir los problemas")
            print("💡 Ejecuta pruebas individuales para debugging específico")
        print("🎮" * 60)

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
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success = main()
    sys.exit(0 if success else 1)