import { useState } from "react";
import styles from "./CartPage.module.css";

export default function CartItem({ item, onRemove }) {
  const [isHovered, setIsHovered] = useState(false);

  const discountedPrice = item.discount > 0 
    ? (item.price * (1 - item.discount / 100)).toFixed(2)
    : null;

  const finalPrice = discountedPrice || item.price.toFixed(2);

  return (
    <div 
      className={`${styles.cartItem} ${isHovered ? styles.cartItemHover : ''}`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <img src={item.image} alt={item.title} className={styles.itemImage} />
      
      <div className={styles.itemInfo}>
        <h3 className={styles.itemTitle}>{item.title}</h3>
        <p className={styles.itemDescription}>{item.description || "Acción y aventura"}</p>
        
        <div className={styles.itemTags}>
          <span className={styles.tag}>🎮 Acción</span>
          <span className={styles.tag}>🌍 Multijugador</span>
        </div>
      </div>

      <div className={styles.itemPricing}>
        {item.discount > 0 && (
          <div className={styles.discountBadge}>-{item.discount}%</div>
        )}
        <div className={styles.priceWrapper}>
          {item.discount > 0 && (
            <span className={styles.originalPrice}>${item.price}</span>
          )}
          <span className={styles.itemPrice}>${finalPrice}</span>
        </div>
      </div>

      <button 
        className={styles.removeButton}
        onClick={() => onRemove(item.id)}
      >
        ✕
      </button>
    </div>
  );
}
