import { useState } from "react";
import GameCard from "../../components/GameCard";
import Button from "../../components/Button";

export default function LibraryPage() {
  const [selectedCollection, setSelectedCollection] = useState("all");

  return (
    <div style={styles.container}>
      <div style={styles.mainContainer}>
        <Sidebar 
          selectedCollection={selectedCollection}
          setSelectedCollection={setSelectedCollection}
        />

        <MainContent />
      </div>
    </div>
  );
}

// Componente: Sidebar
function Sidebar({ selectedCollection, setSelectedCollection }) {
  const collections = [
    "All Games",
    "Favorites",
    "Recently Added"
  ];

  const categories = [
    "Battle Royale Arena",
    "Mystery Mansion",
    "Formula Circuit",
    "Shadow Realms",
    "Counter Strike Legacy",
    "Civilization Dawn",
    "Indie Platformer",
    "Sports Championship"
  ];

  return (
    <aside style={styles.sidebar}>
      <div style={styles.sidebarSection}>
        <div style={styles.sidebarTitle}>⭕ COLLECTIONS</div>
        {collections.map((collection, index) => (
          <div
            key={index}
            style={styles.sidebarItem}
            onClick={() => setSelectedCollection(collection)}
          >
            {collection}
          </div>
        ))}
      </div>

      <div style={styles.sidebarSection}>
        <div style={styles.sidebarTitle}>⭐ CATEGORIES</div>
        {categories.map((category, index) => (
          <div
            key={index}
            style={styles.sidebarItem}
          >
            {category}
          </div>
        ))}
      </div>
    </aside>
  );
}

// Componente: MainContent
function MainContent() {
  return (
    <div style={styles.mainContent}>
      <Section 
        icon="⏱️"
        title="Recently Played"
        games={recentlyPlayedGames}
      />

      <Section 
        icon="⭐"
        title="Recommended For You"
        games={recommendedGames}
      />
    </div>
  );
}

// Componente: Section
function Section({ icon, title, games }) {
  return (
    <div style={styles.section}>
      <div style={styles.sectionHeader}>
        <div style={styles.sectionTitle}>
          <span style={styles.icon}>{icon}</span>
          {title}
        </div>
        <div style={styles.navArrows}>
          <Button variant="ghost" size="small">
            ‹
          </Button>
          <Button variant="ghost" size="small">
            ›
          </Button>
        </div>
      </div>
      <div style={styles.gameGrid}>
        {games.map((game) => (
          <GameCard 
            key={game.id} 
            game={game}
            variant="simple"
            onClick={() => console.log("Playing:", game.title)}
          />
        ))}
      </div>
    </div>
  );
}

// 🎮 DATOS DE EJEMPLO
const recentlyPlayedGames = [
  { 
    id: 1, 
    title: "Cyberpunk Legends", 
    image: "https://images.unsplash.com/photo-1542751371-adc38448a05e?w=400&h=300&fit=crop",
    category: "Action",
    price: 59.99,
    discount: 0,
    multiplayer: true
  },
  { 
    id: 2, 
    title: "Fantasy Quest Online", 
    image: "https://images.unsplash.com/photo-1511512578047-dfb367046420?w=400&h=300&fit=crop",
    category: "RPG",
    price: 49.99,
    discount: 0,
    multiplayer: true
  },
  { 
    id: 3, 
    title: "Speed Racing Ultimate", 
    image: "https://images.unsplash.com/photo-1493711662062-fa541adb3fc8?w=400&h=300&fit=crop",
    category: "Racing",
    price: 39.99,
    discount: 0,
    multiplayer: true
  },
  { 
    id: 4, 
    title: "Dark Chronicles", 
    image: "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=400&h=300&fit=crop",
    category: "Adventure",
    price: 44.99,
    discount: 0,
    multiplayer: false
  },
  { 
    id: 5, 
    title: "Tactical Warfare", 
    image: "https://images.unsplash.com/photo-1526506118085-60ce8714f8c5?w=400&h=300&fit=crop",
    category: "Strategy",
    price: 54.99,
    discount: 0,
    multiplayer: true
  }
];

const recommendedGames = [
  { 
    id: 6, 
    title: "Medieval Kingdoms", 
    image: "https://images.unsplash.com/photo-1518709414768-a88981a4515d?w=400&h=300&fit=crop",
    category: "Strategy",
    price: 34.99,
    discount: 0,
    multiplayer: false
  },
  { 
    id: 7, 
    title: "Pixel Adventure", 
    image: "https://images.unsplash.com/photo-1550745165-9bc0b252726f?w=400&h=300&fit=crop",
    category: "Indie",
    price: 19.99,
    discount: 0,
    multiplayer: false
  },
  { 
    id: 8, 
    title: "Space Odyssey", 
    image: "https://images.unsplash.com/photo-1614732414444-096e5f1122d5?w=400&h=300&fit=crop",
    category: "Simulation",
    price: 64.99,
    discount: 0,
    multiplayer: true
  },
  { 
    id: 9, 
    title: "Dungeon Master", 
    image: "https://images.unsplash.com/photo-1538481199705-c710c4e965fc?w=400&h=300&fit=crop",
    category: "RPG",
    price: 29.99,
    discount: 0,
    multiplayer: false
  },
  { 
    id: 10, 
    title: "Zombie Survival", 
    image: "https://images.unsplash.com/photo-1509347528160-9a9e33742cdb?w=400&h=300&fit=crop",
    category: "Action",
    price: 42.99,
    discount: 0,
    multiplayer: true
  }
];

// Estilos optimizados (sin estilos de botones)
const styles = {
  container: {
    background: 'linear-gradient(135deg, #0a0a0a 0%, #1a1a1a 100%)',
    color: '#e0e0e0',
    minHeight: '100vh'
  },
  mainContainer: {
    display: 'flex',
    padding: '30px',
    gap: '30px'
  },
  sidebar: {
    width: '220px',
    background: 'rgba(20, 20, 20, 0.5)',
    borderRadius: '12px',
    padding: '20px',
    height: 'fit-content'
  },
  sidebarSection: {
    marginBottom: '25px'
  },
  sidebarTitle: {
    color: '#888',
    fontSize: '11px',
    textTransform: 'uppercase',
    letterSpacing: '1px',
    marginBottom: '15px',
    display: 'flex',
    alignItems: 'center',
    gap: '8px'
  },
  sidebarItem: {
    padding: '10px 15px',
    marginBottom: '5px',
    borderRadius: '6px',
    cursor: 'pointer',
    transition: 'all 0.3s',
    fontSize: '14px',
    color: '#ccc'
  },
  mainContent: {
    flex: 1
  },
  section: {
    marginBottom: '50px'
  },
  sectionHeader: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: '20px'
  },
  sectionTitle: {
    fontSize: '20px',
    fontWeight: '600',
    display: 'flex',
    alignItems: 'center',
    gap: '10px'
  },
  icon: {
    color: '#ff6b35'
  },
  navArrows: {
    display: 'flex',
    gap: '10px'
  },
  gameGrid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fill, minmax(180px, 1fr))',
    gap: '20px'
  }
};
