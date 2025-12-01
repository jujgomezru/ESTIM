import uuid
from datetime import datetime, timedelta
from sqlalchemy.orm import Session
from .database import GameDB, SessionLocal

def create_sample_games(db: Session):
    """Inserta juegos de prueba en la base de datos"""
    
    sample_games = [
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Cyberpunk 2077: Phantom Liberty",
            description="Una expansión de rol de acción en mundo abierto con una historia de espionaje thriller.",
            short_description="Expansión de acción RPG cyberpunk",
            price=39.99,
            base_price=49.99,
            is_published=True,
            release_date=datetime(2023, 9, 26),
            age_rating="Mature",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel Core i7-6700",
                    "memory": "12 GB RAM",
                    "graphics": "NVIDIA GeForce GTX 1060",
                    "storage": "70 GB"
                }
            },
            metadata={"genre": ["RPG", "Action", "Cyberpunk"], "features": ["Open World", "Story Rich"]},
            average_rating=4.7,
            review_count=28500,
            download_count=1500000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="The Legend of Zelda: Tears of the Kingdom",
            description="Explora las tierras y los cielos de Hyrule en esta secuela épica de Breath of the Wild.",
            short_description="Aventura de acción y exploración",
            price=59.99,
            base_price=59.99,
            is_published=True,
            release_date=datetime(2023, 5, 12),
            age_rating="Everyone 10+",
            system_requirements={
                "minimum": {
                    "os": "Nintendo Switch",
                    "storage": "18 GB"
                }
            },
            metadata={"genre": ["Adventure", "Action", "RPG"], "features": ["Open World", "Puzzle"]},
            average_rating=4.9,
            review_count=45200,
            download_count=2800000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Baldur's Gate 3",
            description="Un juego de rol basado en Dungeons & Dragons con combate por turnos y narrativa profunda.",
            short_description="RPG de fantasía épica",
            price=59.99,
            base_price=59.99,
            is_published=True,
            release_date=datetime(2023, 8, 3),
            age_rating="Mature",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel I5 4690",
                    "memory": "8 GB RAM",
                    "graphics": "NVIDIA GTX 970",
                    "storage": "150 GB"
                }
            },
            metadata={"genre": ["RPG", "Fantasy", "Turn-Based"], "features": ["Story Rich", "Multiplayer"]},
            average_rating=4.8,
            review_count=36800,
            download_count=5200000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Stardew Valley",
            description="Un juego de simulación de granja donde cultivas, crías animales y construyes relaciones.",
            short_description="Simulador de granja relajante",
            price=14.99,
            base_price=14.99,
            is_published=True,
            release_date=datetime(2016, 2, 26),
            age_rating="Everyone",
            system_requirements={
                "minimum": {
                    "os": "Windows 7",
                    "processor": "2 GHz",
                    "memory": "2 GB RAM",
                    "graphics": "256 mb video memory",
                    "storage": "500 MB"
                }
            },
            metadata={"genre": ["Simulation", "RPG", "Indie"], "features": ["Farming", "Relaxing"]},
            average_rating=4.8,
            review_count=428000,
            download_count=25000000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Call of Duty: Modern Warfare III",
            description="La secuela directa de la aclamada Modern Warfare II con campaña y multijugador.",
            short_description="FPS de acción moderna",
            price=69.99,
            base_price=69.99,
            is_published=True,
            release_date=datetime(2023, 11, 10),
            age_rating="Mature",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel Core i5-6600",
                    "memory": "8 GB RAM",
                    "graphics": "NVIDIA GeForce GTX 960",
                    "storage": "149 GB"
                }
            },
            game_metadata={"genre": ["FPS", "Action", "Shooter"], "features": ["Multiplayer", "Campaign"]},
            average_rating=3.9,
            review_count=12500,
            download_count=850000
        ),
        GameDB(
            publisher_id=uuid.uuid4(),
            title="Hogwarts Legacy",
            description="Un RPG de acción en mundo abierto ambientado en el mundo de Harry Potter del siglo XIX.",
            short_description="RPG de mundo abierto mágico",
            price=59.99,
            base_price=59.99,
            is_published=True,
            release_date=datetime(2023, 2, 10),
            age_rating="Teen",
            system_requirements={
                "minimum": {
                    "os": "Windows 10",
                    "processor": "Intel Core i5-6600",
                    "memory": "16 GB RAM",
                    "graphics": "NVIDIA GeForce GTX 960",
                    "storage": "85 GB"
                }
            },
            metadata={"genre": ["RPG", "Adventure", "Fantasy"], "features": ["Open World", "Magic"]},
            average_rating=4.6,
            review_count=89200,
            download_count=2200000
        )
    ]
    
    try:
        # Verificar si ya existen juegos
        existing_games = db.query(GameDB).count()
        if existing_games > 0:
            print(f"✅ Ya existen {existing_games} juegos en la base de datos")
            return
        
        # Insertar juegos de prueba
        for game in sample_games:
            db.add(game)
        
        db.commit()
        print(f"✅ {len(sample_games)} juegos de prueba insertados correctamente")
        
        # Mostrar los IDs generados para probar
        games = db.query(GameDB).all()
        print("\n🎮 Juegos disponibles (IDs para usar en el carrito):")
        for game in games:
            print(f"   - {game.title}: {game.id}")
            
    except Exception as e:
        db.rollback()
        print(f"❌ Error insertando datos de prueba: {e}")
        raise

def init_sample_data():
    """Función para inicializar datos de prueba"""
    db = SessionLocal()
    try:
        create_sample_games(db)
    finally:
        db.close()

if __name__ == "__main__":
    init_sample_data()