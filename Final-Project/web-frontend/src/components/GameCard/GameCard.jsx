import { useState } from "react";
import styles from "./GameCard.module.css";

export default function GameCard({ 
  game, 
  onClick, 
  showPrice = false,
  showDiscount = false,
  showTags = false,
  onAddToCart,
  variant = "simple"
}) {
  const [isHovered, setIsHovered] = useState(false);

  const discountedPrice = game.discount > 0
    ? (game.price * (1 - game.discount / 100)).toFixed(2)
    : null;

  return (
    <div
      className={`${styles.gameCard} ${isHovered ? styles.hovered : ''}`}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
      onClick={onClick}
    >
      <div className={styles.imageWrapper}>
        <img 
          src={game.image} 
          alt={game.title} 
          className={styles.gameImage}
        />
        
        {/* Discount Badge */}
        {showDiscount && game.discount > 0 && (
          <div className={styles.discountBadge}>-{game.discount}%</div>
        )}

        {/* Hover Overlay */}
        {variant === "detailed" && isHovered && onAddToCart && (
          <div className={styles.overlay}>
            <button
              className={styles.addToCartBtn}
              onClick={(e) => {
                e.stopPropagation();
                onAddToCart(game.id);
              }}
            >
              🛒 Añadir
            </button>
          </div>
        )}
      </div>

      <div className={styles.gameInfo}>
        <h3 className={styles.gameTitle}>{game.title}</h3>
        
        {/* Tags */}
        {showTags && game.category && (
          <div className={styles.tags}>
            <span className={styles.tag}>{game.category}</span>
            {game.multiplayer && <span className={styles.tag}>Multiplayer</span>}
          </div>
        )}

        {/* Price */}
        {showPrice && game.price !== undefined && (
          <div className={styles.priceContainer}>
            {game.discount > 0 ? (
              <>
                <span className={styles.originalPrice}>${game.price}</span>
                <span className={styles.finalPrice}>${discountedPrice}</span>
              </>
            ) : (
              <span className={styles.price}>${game.price.toFixed(2)}</span>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
