import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllGames } from "../gamesService";
import { addToCart } from "../../cart/cartService";
import GameCard from "../../../components/GameCard/GameCard";
import Button from "../../../components/Button";
import LoadingSpinner from "../../../components/LoadingSpinner";
import styles from "./StorePage.module.css";

export default function StorePage() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentSlide, setCurrentSlide] = useState(0);
  const navigate = useNavigate();

  useEffect(() => {
    loadGames();
  }, []);

  async function loadGames() {
    try {
      const data = await getAllGames();
      setGames(data || []);
    } catch (error) {
      console.error("Error loading games:", error);
      setGames([]);
    } finally {
      setLoading(false);
    }
  }

  async function handleAddToCart(gameId) {
    try {
      await addToCart(gameId);
      alert("¡Juego agregado al carrito!");
    } catch (error) {
      alert("Error al agregar al carrito");
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando tienda..." />;
  }

  const featuredGames = games.slice(0, 3);
  const popularGames = games.slice(0, 4);

  return (
    <div className={styles.container}>
      <HeroCarousel 
        games={featuredGames}
        currentSlide={currentSlide}
        setCurrentSlide={setCurrentSlide}
        onAddToCart={handleAddToCart}
      />

      <div className={styles.content}>

        <Section 
          icon="🔥"
          title="Most Popular"
          games={popularGames}
          onAddToCart={handleAddToCart}
        />
      </div>
    </div>
  );
}

function HeroCarousel({ games, currentSlide, setCurrentSlide, onAddToCart }) {
  const [isHovered, setIsHovered] = useState(false);

  useEffect(() => {
    if (!isHovered && games.length > 0) {
      const interval = setInterval(() => {
        setCurrentSlide((prev) => (prev + 1) % games.length);
      }, 5000);
      return () => clearInterval(interval);
    }
  }, [isHovered, games.length, setCurrentSlide]);

  if (!games.length) return null;

  const currentGame = games[currentSlide] || {};

  return (
    <div 
      className={styles.heroCarousel}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      <div className={styles.heroImageContainer}>
        <img 
          src={currentGame.image || "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=1200&h=600&fit=crop"} 
          alt={currentGame.title}
          className={styles.heroImage}
        />
        <div className={styles.heroOverlay} />
      </div>

      <div className={styles.heroContent}>
        <div className={styles.heroTags}>
          <span className={styles.heroTag}>{currentGame.category || "Action"}</span>
          <span className={styles.heroTag}>RPG</span>
          <span className={styles.heroTag}>Open World</span>
        </div>

        <h1 className={styles.heroTitle}>{currentGame.title || "Cyber Legends 2077"}</h1>
        <p className={styles.heroDescription}>
          {currentGame.description || "Immerse yourself in a futuristic city full of dangers and adventures. Every decision counts."}
        </p>

        <div className={styles.heroPricing}>
          {currentGame.discount > 0 && (
            <div className={styles.heroDiscount}>-{currentGame.discount}%</div>
          )}
          <div className={styles.heroPriceContainer}>
            {currentGame.discount > 0 && (
              <span className={styles.heroOriginalPrice}>${currentGame.price}</span>
            )}
            <span className={styles.heroFinalPrice}>
              ${currentGame.discount > 0 
                ? (currentGame.price * (1 - currentGame.discount / 100)).toFixed(2)
                : currentGame.price?.toFixed(2) || "59.99"}
            </span>
          </div>
        </div>

        <Button variant="primary" size="large" onClick={() => onAddToCart(currentGame.id)}>
          Buy Now
        </Button>

        <div className={styles.carouselDots}>
          {games.map((_, index) => (
            <div
              key={index}
              className={`${styles.carouselDot} ${index === currentSlide ? styles.carouselDotActive : ''}`}
              onClick={() => setCurrentSlide(index)}
            />
          ))}
        </div>

        <Button 
          variant="icon" 
          size="icon"
          onClick={() => setCurrentSlide((currentSlide + 1) % games.length)}
          style={{ position: 'absolute', top: '50%', right: '40px', transform: 'translateY(-50%)', zIndex: 20 }}
        >
          ›
        </Button>
      </div>
    </div>
  );
}

function Section({ icon, title, games, onAddToCart, showDiscount }) {
  const navigate = useNavigate();

  return (
    <section className={styles.section}>
      <div className={styles.sectionHeader}>
        <div className={styles.sectionTitle}>
          <span className={styles.sectionIcon}>{icon}</span>
          {title}
        </div>
      </div>

      <div className={styles.gameGrid}>
        {games.map((game) => (
          <GameCard
            key={game.id}
            game={game}
            variant="detailed"
            showPrice={true}
            showDiscount={showDiscount}
            showTags={true}
            onAddToCart={onAddToCart}
            onClick={() => navigate(`/game/${game.id}`)}
          />
        ))}
      </div>
    </section>
  );
}
