# database.py

import os
import uuid
from datetime import datetime
from sqlalchemy import (
    create_engine, Column, String, Text, Boolean, DateTime, Date,
    JSON, Numeric, Integer, BigInteger
)
from sqlalchemy.orm import declarative_base, sessionmaker
from sqlalchemy.dialects.postgresql import UUID as PG_UUID


# ==============================
# 1) ELEGIR AUTOMÁTICAMENTE LA DB
# ==============================

render_db_url = os.getenv("DATABASE_URL")

if render_db_url:
    DATABASE_URL = render_db_url
    print("🟣 Usando base de datos de RENDER")
else:
    db_user = os.getenv("DB_USER", "estim")
    db_pass = os.getenv("DB_PASS", "estim")
    db_host = os.getenv("DB_HOST", "localhost")
    db_name = os.getenv("DB_NAME", "estim")
    db_port = os.getenv("DB_PORT", "5432")

    DATABASE_URL = f"postgresql://{db_user}:{db_pass}@{db_host}:{db_port}/{db_name}"
    print("🟢 Usando base de datos LOCAL")

print(f"🔗 Conectando a PostgreSQL: {DATABASE_URL}")


# ==============================
# 2) CONEXIÓN SQLALCHEMY
# ==============================

engine = create_engine(DATABASE_URL)
Base = declarative_base()

SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


# ==============================
# 3) MODELO ORM
# ==============================

class GameDB(Base):
    __tablename__ = "games"

    id = Column(PG_UUID(as_uuid=True), primary_key=True, index=True)
    publisher_id = Column(PG_UUID(as_uuid=True), nullable=False, index=True)

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
# 4) CREAR TABLAS *DESPUÉS* DE DEFINIR MODELOS
# ==============================

Base.metadata.create_all(bind=engine)


# ==============================
# 5) SESIÓN PARA FASTAPI
# ==============================

def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
