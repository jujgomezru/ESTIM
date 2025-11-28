from sqlalchemy.orm import Session
from sqlalchemy import or_
from typing import List, Dict, Optional, Any
from database import GameDB  # ← CAMBIÉ ESTO: sin punto
import logging

logger = logging.getLogger(__name__)

class SearchService:
    """Servicio para manejar todas las operaciones de búsqueda de juegos"""
    
    @staticmethod
    def search_by_genre(
        db: Session,
        genre: str,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        """
        Buscar juegos por género - VERSIÓN 100% CONFIABLE
        """
        print("🎯 MÉTODO search_by_genre NUEVO EJECUTÁNDOSE")
        
        try:
            # Validar parámetros
            if not genre or not isinstance(genre, str):
                return []
            
            clean_genre = genre.strip().lower()
            if not clean_genre:
                return []
            
            # Obtener todos los juegos publicados
            query = db.query(GameDB).filter(GameDB.is_published == True)
            all_games = query.order_by(GameDB.average_rating.desc()).all()
            
            print(f"📊 Juegos obtenidos para filtrar: {len(all_games)}")
            
            # Filtrar por género en memoria
            filtered_games = []
            
            for game in all_games:
                try:
                    # Verificar si el juego tiene metadata
                    if not game.game_metadata:
                        continue
                    
                    # Obtener géneros del juego
                    game_genres = game.game_metadata.get('genre', [])
                    
                    # Si es string, convertirlo a lista
                    if isinstance(game_genres, str):
                        game_genres = [game_genres]
                    
                    # Buscar coincidencia
                    genre_found = False
                    for g in game_genres:
                        if g and isinstance(g, str) and clean_genre in g.lower():
                            genre_found = True
                            break
                    
                    if genre_found:
                        filtered_games.append(game)
                        print(f"✅ {game.title} - Géneros: {game_genres}")
                        
                except Exception as game_error:
                    print(f"⚠️ Error procesando {game.title}: {game_error}")
                    continue
            
            print(f"🎯 Juegos que coinciden con '{genre}': {len(filtered_games)}")
            
            # Aplicar paginación
            safe_skip = max(0, skip)
            safe_limit = max(1, min(limit, 100))
            
            start_idx = min(safe_skip, len(filtered_games))
            end_idx = min(safe_skip + safe_limit, len(filtered_games))
            paginated_games = filtered_games[start_idx:end_idx]
            
            # Formatear respuesta
            results = []
            for game in paginated_games:
                try:
                    game_data = {
                        "id": str(game.id),
                        "title": game.title,
                        "price": float(game.price) if game.price else 0.0,
                        "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                        "description": game.short_description or "",
                        "genres": game.game_metadata.get("genre", []) if game.game_metadata else [],
                        "game_metadata": game.game_metadata
                    }
                    results.append(game_data)
                except Exception as e:
                    print(f"⚠️ Error formateando {game.title}: {e}")
                    continue
            
            return results
            
        except Exception as e:
            print(f"💥 ERROR en search_by_genre: {e}")
            import traceback
            traceback.print_exc()
            return []

    # Mantener los otros métodos existentes pero simplificados...
    @staticmethod
    def search_games(
        db: Session,
        search_term: str = "",
        min_price: Optional[float] = None,
        max_price: Optional[float] = None,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        """Búsqueda básica de juegos"""
        try:
            query = db.query(GameDB).filter(GameDB.is_published == True)
            
            if search_term and search_term.strip():
                term = f"%{search_term.strip()}%"
                query = query.filter(
                    or_(
                        GameDB.title.ilike(term),
                        GameDB.description.ilike(term),
                        GameDB.short_description.ilike(term)
                    )
                )
            
            query = query.order_by(GameDB.average_rating.desc(), GameDB.title.asc())
            games = query.offset(skip).limit(limit).all()
            
            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            
            return results
            
        except Exception as e:
            logger.error(f"Error en búsqueda básica: {str(e)}")
            return []

    @staticmethod
    def advanced_search(
        db: Session,
        title: Optional[str] = None,
        description: Optional[str] = None,
        min_rating: Optional[float] = None,
        max_rating: Optional[float] = None,
        age_rating: Optional[str] = None,
        skip: int = 0,
        limit: int = 20
    ) -> List[Dict]:
        """Búsqueda avanzada"""
        try:
            query = db.query(GameDB).filter(GameDB.is_published == True)
            
            if title and title.strip():
                query = query.filter(GameDB.title.ilike(f"%{title.strip()}%"))
            
            if description and description.strip():
                query = query.filter(
                    or_(
                        GameDB.description.ilike(f"%{description.strip()}%"),
                        GameDB.short_description.ilike(f"%{description.strip()}%")
                    )
                )
            
            query = query.order_by(GameDB.average_rating.desc(), GameDB.release_date.desc())
            games = query.offset(skip).limit(limit).all()
            
            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            
            return results
            
        except Exception as e:
            logger.error(f"Error en búsqueda avanzada: {str(e)}")
            return []

    @staticmethod
    def get_popular_games(
        db: Session,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        """Obtener juegos populares"""
        try:
            query = db.query(GameDB).filter(GameDB.is_published == True)
            games = query.order_by(
                GameDB.average_rating.desc(),
                GameDB.download_count.desc()
            ).offset(skip).limit(limit).all()
            
            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            
            return results
            
        except Exception as e:
            logger.error(f"Error obteniendo juegos populares: {str(e)}")
            return []

    @staticmethod
    def get_recent_games(
        db: Session,
        skip: int = 0,
        limit: int = 10
    ) -> List[Dict]:
        """Obtener juegos recientes"""
        try:
            query = db.query(GameDB).filter(
                GameDB.is_published == True,
                GameDB.release_date.isnot(None)
            )
            games = query.order_by(GameDB.release_date.desc()).offset(skip).limit(limit).all()
            
            results = []
            for game in games:
                game_data = {
                    "id": str(game.id),
                    "title": game.title,
                    "price": float(game.price) if game.price else 0.0,
                    "average_rating": float(game.average_rating) if game.average_rating else 0.0,
                    "description": game.short_description or "",
                }
                results.append(game_data)
            
            return results
            
        except Exception as e:
            logger.error(f"Error obteniendo juegos recientes: {str(e)}")
            return []