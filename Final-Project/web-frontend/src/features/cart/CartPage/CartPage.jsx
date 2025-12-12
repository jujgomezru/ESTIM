import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Button from "../../../components/Button";
import { getCart, removeFromCart, clearCart } from "../cartService";
import CartItem from "./CartItem";
import styles from "./CartPage.module.css";

export default function CartPage() {
  const [cartItems, setCartItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    loadCart();
  }, []);

  async function loadCart() {
    try {
      const data = await getCart();
      setCartItems(data);
    } catch (error) {
      console.error("Error loading cart:", error);
    } finally {
      setLoading(false);
    }
  }

  async function handleRemove(gameId) {
    try {
      await removeFromCart(gameId);
      setCartItems(cartItems.filter(item => item.id !== gameId));
    } catch (error) {
      alert("Error al eliminar del carrito");
    }
  }

  async function handleCheckout() {
    try {
      await clearCart();
      navigate("/purchase-success");
    } catch (error) {
      alert("Error al procesar la compra");
    }
  }

  const safeCartItems = Array.isArray(cartItems) ? cartItems : [];

  const subtotal = safeCartItems.reduce((sum, item) => {
    const price = item.discount > 0
      ? item.price * (1 - item.discount / 100)
      : item.price;
    return sum + price;
  }, 0);

  const tax = subtotal * 0.19;
  const total = subtotal + tax;

  if (loading) {
    return (
      <div className={styles.loadingContainer}>
        <div className={styles.spinner}>⏳</div>
        <p className={styles.loadingText}>Cargando carrito...</p>
      </div>
    );
  }

  return (
    <div className={styles.container}>
      <div className={styles.header}>
        <button className={styles.backButton} onClick={() => navigate(-1)}>
          ← Volver
        </button>
        <h1 className={styles.title}>🛒 Mi Carrito</h1>
      </div>

      {safeCartItems.length === 0 ? (
        <EmptyCart navigate={navigate} />
      ) : (
        <div className={styles.content}>
          <div className={styles.itemsSection}>
            <div className={styles.sectionHeader}>
              <h2 className={styles.sectionTitle}>Artículos ({safeCartItems.length})</h2>
              <button 
                className={styles.clearButton}
                onClick={() => {
                  if (window.confirm("¿Vaciar todo el carrito?")) {
                    setCartItems([]);
                  }
                }}
              >
                🗑️ Vaciar carrito
              </button>
            </div>

            <div className={styles.itemsList}>
              {safeCartItems.map((item) => (
                <CartItem 
                  key={item.id} 
                  item={item} 
                  onRemove={handleRemove}
                />
              ))}
            </div>
          </div>

          <div className={styles.summarySection}>
            <div className={styles.summaryCard}>
              <h2 className={styles.summaryTitle}>Resumen de compra</h2>
              
              <div className={styles.summaryDetails}>
                <div className={styles.summaryRow}>
                  <span className={styles.summaryLabel}>Subtotal</span>
                  <span className={styles.summaryValue}>${subtotal.toFixed(2)}</span>
                </div>
                
                <div className={styles.summaryRow}>
                  <span className={styles.summaryLabel}>Impuestos (19%)</span>
                  <span className={styles.summaryValue}>${tax.toFixed(2)}</span>
                </div>

                <div className={styles.divider}></div>

                <div className={styles.summaryRow}>
                  <span className={styles.totalLabel}>Total</span>
                  <span className={styles.totalValue}>${total.toFixed(2)}</span>
                </div>
              </div>

              <Button onClick={() => navigate("/cart/success")} fullWidth>
                💳 Proceder al pago
              </Button>

              <div className={styles.securityBadge}>
                <span className={styles.securityIcon}>🔒</span>
                <span className={styles.securityText}>Pago 100% seguro</span>
              </div>
            </div>

            <div className={styles.infoCard}>
              <h3 className={styles.infoTitle}>💡 Información</h3>
              <ul className={styles.infoList}>
                <li className={styles.infoItem}>✓ Descarga instantánea</li>
                <li className={styles.infoItem}>✓ Actualizaciones automáticas</li>
                <li className={styles.infoItem}>✓ Soporte 24/7</li>
                <li className={styles.infoItem}>✓ Garantía de reembolso</li>
              </ul>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

function EmptyCart({ navigate }) {
  return (
    <div className={styles.emptyCart}>
      <div className={styles.emptyIcon}>🛒</div>
      <h2 className={styles.emptyTitle}>Tu carrito está vacío</h2>
      <p className={styles.emptyText}>
        Explora nuestra tienda y encuentra juegos increíbles
      </p>
      
      <div className={styles.buttonGroup}>
        <Button 
          variant="primary"
          size="large"
          icon="🎮"
          onClick={() => navigate("/")}
        >
          Ir a la tienda
        </Button>
        
      </div>
    </div>
  );
}
