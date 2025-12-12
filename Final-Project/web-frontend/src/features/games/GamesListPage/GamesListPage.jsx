import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getAllGames } from "../gamesService";
import { addToCart } from "../../cart/cartService";
import GameCard from "../../../components/GameCard/GameCard";
import Button from "../../../components/Button";
import LoadingSpinner from "../../../components/LoadingSpinner";
import Badge from "../../../components/Badge";
import styles from "./GamesListPage.module.css";

export default function GamesListPage() {
  const [games, setGames] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [searchQuery, setSearchQuery] = useState("");
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
      navigate(`/game/${gameId}`);
    } catch (error) {
      alert("Error al agregar al carrito");
      navigate(`/game/${gameId}`);
    }
  }

  if (loading) {
    return <LoadingSpinner message="Cargando juegos..." />;
  }

  const filteredGames = games.filter(game => {
    const matchesCategory = selectedCategory === "all" || game.category === selectedCategory;
    const matchesSearch = game.title.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  });

  const categories = ["all", ...new Set(games.map(g => g.category))];

  return (
    <div className={styles.container}>
      {/* Header Section */}
      <div className={styles.headerSection}>
        <div>
          <h1 className={styles.pageTitle}>Todos los Juegos</h1>
          <p className={styles.pageSubtitle}>
            Explora nuestra colección completa de {games.length} juegos
          </p>
        </div>
        <Button 
          variant="primary" 
          size="large"
          icon="🛒"
          onClick={() => navigate("/cart")}
        >
          Ver Carrito
        </Button>
      </div>

      {/* Filters Section */}
      <div className={styles.filtersSection}>
        {/* Search Bar */}
        <div className={styles.searchContainer}>
          <input
            type="text"
            placeholder="🔍 Buscar juegos..."
            className={styles.searchInput}
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        {/* Category Filters */}
        <div className={styles.categoryFilters}>
          {categories.map((category) => (
            <Button
              key={category}
              variant={selectedCategory === category ? "primary" : "ghost"}
              size="small"
              onClick={() => setSelectedCategory(category)}
            >
              {category === "all" ? "Todos" : category}
            </Button>
          ))}
        </div>
      </div>

      {/* Results Info */}
      <div className={styles.resultsInfo}>
        <p className={styles.resultsText}>
          Mostrando <strong>{filteredGames.length}</strong> juegos
        </p>
        {selectedCategory !== "all" && (
          <Badge variant="category">{selectedCategory}</Badge>
        )}
        {searchQuery && (
          <Badge variant="default">Buscando: "{searchQuery}"</Badge>
        )}
      </div>

      {/* Games Grid */}
      {filteredGames.length > 0 ? (
        <div className={styles.gamesGrid}>
          {filteredGames.map((game) => (
            <GameCard
              key={game.id}
              game={game}
              variant="detailed"
              showPrice={true}
              showDiscount={true}
              showTags={true}
              onAddToCart={handleAddToCart}
              onClick={() => navigate(`/game/${game.id}`)}
            />
          ))}
        </div>
      ) : (
        <EmptyState 
          searchQuery={searchQuery}
          onReset={() => {
            setSearchQuery("");
            setSelectedCategory("all");
          }}
        />
      )}
    </div>
  );
}

function EmptyState({ searchQuery, onReset }) {
  return (
    <div className={styles.emptyState}>
      <div className={styles.emptyIcon}>🔍</div>
      <h2 className={styles.emptyTitle}>No se encontraron juegos</h2>
      <p className={styles.emptyText}>
        {searchQuery 
          ? `No hay resultados para "${searchQuery}"`
          : "Intenta con otros filtros"
        }
      </p>
      <Button variant="primary" onClick={onReset}>
        Limpiar filtros
      </Button>
    </div>
  );
}
