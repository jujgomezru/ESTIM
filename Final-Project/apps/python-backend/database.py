# database.py

import os
import uuid
from datetime import datetime
import sqlalchemy as sa
from sqlalchemy import (
    create_engine, Column, String, Text, Boolean, DateTime, Date,
    JSON, Numeric, Integer, BigInteger
)
from sqlalchemy.orm import declarative_base, sessionmaker

# ==============================
# 1) SELECCIÓN AUTOMÁTICA DE DB
# ==============================

if os.getenv("GITHUB_ACTIONS") == "true":
    # >>> USADO SOLO EN CI/CD <<< SQLite
    DATABASE_URL = "sqlite:///./test.db"
    print("🟡 Usando SQLite para GitHub Actions")
else:
    render_db_url = os.getenv("DATABASE_URL")
    if render_db_url:
        DATABASE_URL = render_db_url
        print("🟣 Usando base de datos de RENDER")
    else:
        # Local
        db_user = os.getenv("DB_USER", "estim")
        db_pass = os.getenv("DB_PASS", "estim")
        db_host = os.getenv("DB_HOST", "localhost")
        db_name = os.getenv("DB_NAME", "estim")
        db_port = os.getenv("DB_PORT", "5432")
        DATABASE_URL = f"postgresql://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}"
        print("🟢 Usando base de datos LOCAL")

print(f"🔗 Conectando a BD: {DATABASE_URL}")

# ==============================
# 2) TIPO UUID COMPATIBLE
# ==============================

if DATABASE_URL.startswith("sqlite"):
    UUID_TYPE = String(36)  # SQLite no soporta UUID, usamos string
else:
    from sqlalchemy.dialects.postgresql import UUID as PG_UUID
    UUID_TYPE = PG_UUID(as_uuid=True)

# ==============================
# 3) CONEXIÓN SQLALCHEMY
# ==============================

engine = create_engine(DATABASE_URL, connect_args={"check_same_thread": False} if DATABASE_URL.startswith("sqlite") else {})
Base = declarative_base()
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# ==============================
# 4) MODELO ORM
# ==============================

class GameDB(Base):
    __tablename__ = "games"

    id = Column(UUID_TYPE, primary_key=True, index=True, default=lambda: str(uuid.uuid4()))
    publisher_id = Column(UUID_TYPE, nullable=False, index=True)

    title = Column(String(255), nullable=False, index=True)
    description = Column(Text)
    short_description = Column(String(500))

    price = Column(Numeric(10, 2), nullable=False, index=True)
    base_price = Column(Numeric(10, 2))

    is_published = Column(Boolean, default=False, index=True)
    release_date = Column(Date)
    age_rating = Column(String(50))

    system_requirements = Column(JSON)
    game_metadata = Column("metadata", JSON)

    average_rating = Column(Numeric(3, 2), default=0.0)
    review_count = Column(Integer, default=0)
    total_playtime = Column(BigInteger, default=0)
    download_count = Column(Integer, default=0)

    created_at = Column(DateTime(timezone=True), default=datetime.utcnow)
    updated_at = Column(DateTime(timezone=True), default=datetime.utcnow, onupdate=datetime.utcnow)

# ==============================
# 5) CREAR TABLAS (SI NO EXISTEN)
# ==============================

Base.metadata.create_all(bind=engine)

# ==============================
# 6) SESIÓN PARA FASTAPI
# ==============================

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()

# CI/CD funcionando correctamente

