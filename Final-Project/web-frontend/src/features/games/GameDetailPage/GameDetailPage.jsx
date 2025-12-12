import { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getGameById } from "../gamesService";
import { addToCart } from "../../cart/cartService";
import { addToWishlist } from "../../wishlist/wishlistService";
import LoadingSpinner from "../../../components/LoadingSpinner";
import Button from "../../../components/Button/Button";
import Badge from "../../../components/Badge";
import styles from "./GameDetailPage.module.css";

export default function GameDetailPage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [game, setGame] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedImage, setSelectedImage] = useState(0);

  useEffect(() => {
    loadGame();
  }, [id]);

  async function loadGame() {
    try {
      const data = await getGameById(id);
      setGame(data);
    } catch (error) {
      console.error("Error loading game:", error);
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando detalles..." />;
  }

  if (!game) {
    return <EmptyState navigate={navigate} />;
  }

  return (
    <div className={styles.container}>
      {/* Breadcrumb */}
      <Breadcrumb game={game} navigate={navigate} />

      {/* Main Layout */}
      <div className={styles.mainLayout}>
        {/* Left: Images + Content */}
        <div className={styles.leftColumn}>
          <ImageGallery 
            game={game}
            selectedImage={selectedImage}
            setSelectedImage={setSelectedImage}
          />
          
          <AboutSection game={game} />
          
          <ReviewsSection game={game} />
        </div>

        {/* Right: Purchase Panel */}
        <PurchasePanel game={game} />
      </div>
    </div>
  );
}

function Breadcrumb({ game, navigate }) {
  return (
    <div className={styles.breadcrumb}>
      <span className={styles.breadcrumbLink} onClick={() => navigate("/")}>Home</span>
      <span className={styles.separator}>›</span>
      <span className={styles.breadcrumbLink} onClick={() => navigate(`/games?category=${game.category}`)}>{game.category}</span>
      <span className={styles.separator}>›</span>
      <span className={styles.breadcrumbCurrent}>{game.title}</span>
    </div>
  );
}

function ImageGallery({ game, selectedImage, setSelectedImage }) {
  const images = game.images || [game.image];

  return (
    <div className={styles.gallerySection}>
      {/* Main Image */}
      <div className={styles.mainImageContainer}>
        <img 
          src={images[selectedImage]} 
          alt={game.title}
          className={styles.mainImage}
        />
      </div>

      {/* Thumbnails */}
      <div className={styles.thumbnailsRow}>
        {images.map((img, index) => (
          <div
            key={index}
            className={`${styles.thumbnail} ${selectedImage === index ? styles.thumbnailActive : ''}`}
            onClick={() => setSelectedImage(index)}
          >
            <img src={img} alt={`Screenshot ${index + 1}`} className={styles.thumbnailImg} />
          </div>
        ))}
      </div>
    </div>
  );
}

function AboutSection({ game }) {
  return (
    <div className={styles.aboutSection}>
      <h2 className={styles.sectionTitle}>About This Game</h2>
      <p className={styles.aboutText}>
        {game.longDescription || game.description}
      </p>

      <div className={styles.metaGrid}>
        <div className={styles.metaItem}>
          <span className={styles.metaLabel}>Developer:</span>
          <span className={styles.metaValue}>{game.developer}</span>
        </div>
        <div className={styles.metaItem}>
          <span className={styles.metaLabel}>Publisher:</span>
          <span className={styles.metaValue}>{game.publisher}</span>
        </div>
      </div>
    </div>
  );
}

function ReviewsSection({ game }) {
  return (
    <div className={styles.reviewsSection}>
      <h2 className={styles.sectionTitle}>Reviews</h2>
      <div className={styles.reviewsContent}>
        <div className={styles.ratingBadge}>
          <span className={styles.thumbsUp}>👍</span>
          <span className={styles.ratingPercent}>{game.rating}%</span>
        </div>
        <div className={styles.reviewsInfo}>
          <div className={styles.reviewBar}>
            <div className={styles.reviewBarFill} style={{ width: `${game.rating}%` }} />
          </div>
          <p className={styles.reviewCount}>{game.reviewCount?.toLocaleString()} user reviews</p>
        </div>
      </div>
    </div>
  );
}

function PurchasePanel({ game }) {
  async function handleAddToCart() {
    try {
      await addToCart(game.id);
      alert("¡Juego agregado al carrito!");
    } catch (error) {
      alert("Error al agregar al carrito");
    }
  }

  async function handleAddToWishlist() {
    try {
      await addToWishlist(game.id);
      alert("¡Agregado a la wishlist!");
    } catch (error) {
      alert("Error al agregar a wishlist");
    }
  }

  return (
    <div className={styles.purchasePanel}>
      {/* Header with game image */}
      <div className={styles.panelHeader}>
        <img src={game.image} alt={game.title} className={styles.panelHeaderImg} />
      </div>

      {/* Content */}
      <div className={styles.panelBody}>
        <h1 className={styles.panelTitle}>{game.title}</h1>
        <p className={styles.panelSubtitle}>{game.description}</p>

        {/* Tags */}
        <div className={styles.tagsRow}>
          {game.tags?.map((tag, idx) => (
            <Badge key={idx} variant="category" size="small">{tag}</Badge>
          ))}
        </div>

        {/* Release Date */}
        <div className={styles.infoRow}>
          <span className={styles.infoIcon}>📅</span>
          <div>
            <div className={styles.infoLabel}>Release Date:</div>
            <div className={styles.infoValue}>{game.releaseDate}</div>
          </div>
        </div>

        {/* Platforms */}
        <div className={styles.infoRow}>
          <span className={styles.infoIcon}>💻</span>
          <div>
            <div className={styles.infoLabel}>Platforms:</div>
            <div className={styles.platformBadges}>
              {game.platforms?.map((p, idx) => (
                <Badge key={idx} variant="default" size="small">{p}</Badge>
              ))}
            </div>
          </div>
        </div>

        {/* Price */}
        <div className={styles.priceBox}>
          <div className={styles.priceLabel}>Price:</div>
          <div className={styles.priceValue}>${game.price?.toFixed(2)}</div>
        </div>

        {/* Buttons */}
        <div className={styles.actionsBox}>
          <Button 
            variant="primary" 
            size="large"
            icon="🛒"
            fullWidth
            onClick={handleAddToCart}
          >
            Add to Cart
          </Button>

          <div className={styles.secondaryRow}>
            <Button 
              variant="ghost" 
              size="medium"
              icon="💝"
              onClick={handleAddToWishlist}
              style={{ flex: 1 }}
            >
              Add to Wishlist
            </Button>
            <Button 
              variant="ghost" 
              size="medium"
              icon="🔗"
            >
              
            </Button>
            <Button 
              variant="ghost" 
              size="medium"
              icon="🔗"
            >
              
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}

function EmptyState({ navigate }) {
  return (
    <div className={styles.emptyState}>
      <div className={styles.emptyIcon}>🎮</div>
      <h1>Game not found</h1>
      <Button variant="primary" onClick={() => navigate("/games")}>
        Back to Store
      </Button>
    </div>
  );
}
