import os
from sqlalchemy import create_engine, Column, String, Text, Boolean, DateTime, JSON, Numeric, Integer
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker
from sqlalchemy.dialects.postgresql import UUID
import uuid
from datetime import datetime

db_user = os.getenv('DB_USER', 'estim')
db_pass = os.getenv('DB_PASS', 'estim')
db_host = os.getenv('DB_HOST', 'localhost')
db_name = os.getenv('DB_NAME', 'estim')

db_port_str = os.getenv('DB_PORT', '5432')
if db_port_str == 'None':
    db_port = 5432
else:
    try:
        db_port = int(db_port_str) 
    except (ValueError, TypeError):
        db_port = 5432 

DATABASE_URL = f"postgresql://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}"

print(f"🔗 Conectando a PostgreSQL: {db_host}:{db_port}/{db_name}")

engine = create_engine(DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()

class GameDB(Base):
    __tablename__ = "games"
    
    id = Column(UUID, primary_key=True, default=uuid.uuid4, index=True)
    publisher_id = Column(UUID, nullable=False, index=True)
    title = Column(String(255), nullable=False, index=True)
    description = Column(Text)
    short_description = Column(String(500))
    price = Column(Numeric(10, 2), nullable=False, index=True)
    base_price = Column(Numeric(10, 2))
    is_published = Column(Boolean, default=False, index=True)
    release_date = Column(DateTime, index=True)
    age_rating = Column(String(50))
    system_requirements = Column(JSON)
    game_metadata = Column('metadata', JSON)
    average_rating = Column(Numeric(3, 2), default=0.0, index=True)
    review_count = Column(Integer, default=0)
    total_playtime = Column(Integer, default=0)
    download_count = Column(Integer, default=0)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)

def create_tables():
    print("🔧 Creando tablas si no existen...")
    Base.metadata.create_all(bind=engine)
    print("✅ Tablas verificadas/creadas")

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()