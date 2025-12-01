# database.py

import os
import uuid
from datetime import datetime

from sqlalchemy import (
    create_engine,
    Column,
    String,
    Text,
    Boolean,
    DateTime,
    Date,
    JSON,
    Numeric,
    Integer,
    BigInteger,
)
from sqlalchemy.orm import declarative_base, sessionmaker
from sqlalchemy.dialects.postgresql import UUID as PG_UUID

# ==================== CONFIG DB (USANDO ESQUEMA OFICIAL) ====================

db_user = os.getenv("DB_USER", "estim")
db_pass = os.getenv("DB_PASS", "estim")
db_host = os.getenv("DB_HOST", "localhost")
db_name = os.getenv("DB_NAME", "estim")

db_port_str = os.getenv("DB_PORT", "5432")
if db_port_str == "None":
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


# ==================== MODELO ORM PARA LA TABLA games ====================

class GameDB(Base):
    """
    Mapeo ORM de la tabla games definida en el esquema oficial:

      CREATE TABLE games (
        id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
        publisher_id         UUID NOT NULL REFERENCES publishers(id) ON DELETE CASCADE,
        title                VARCHAR(255) NOT NULL,
        description          TEXT,
        short_description    VARCHAR(500),
        price                NUMERIC(10,2) NOT NULL,
        base_price           NUMERIC(10,2),
        is_published         BOOLEAN NOT NULL DEFAULT FALSE,
        release_date         DATE,
        age_rating           age_rating_type,
        system_requirements  JSONB,
        metadata             JSONB,
        average_rating       NUMERIC(3,2) NOT NULL DEFAULT 0.0,
        review_count         INTEGER NOT NULL DEFAULT 0,
        total_playtime       BIGINT  NOT NULL DEFAULT 0,
        download_count       INTEGER NOT NULL DEFAULT 0,
        created_at           TIMESTAMPTZ NOT NULL DEFAULT NOW(),
        updated_at           TIMESTAMPTZ NOT NULL DEFAULT NOW()
      );
    """

    __tablename__ = "games"

    # Usamos el tipo UUID de PostgreSQL.
    # IMPORTANTE: Esto NO crea la tabla, solo mapea la existente.
    id = Column(
        PG_UUID(as_uuid=True),
        primary_key=True,
        index=True,
        default=uuid.uuid4,  # opcional, DB ya tiene DEFAULT gen_random_uuid()
    )

    publisher_id = Column(PG_UUID(as_uuid=True), nullable=False, index=True)

    title = Column(String(255), nullable=False, index=True)
    description = Column(Text)
    short_description = Column(String(500))

    price = Column(Numeric(10, 2), nullable=False, index=True)
    base_price = Column(Numeric(10, 2))

    is_published = Column(Boolean, nullable=False, default=False, index=True)

    # En el esquema es DATE (sin hora)
    release_date = Column(Date)

    # En la BD es un enum age_rating_type; aquí lo tratamos como String
    # (no pasa nada mientras no llamemos create_all).
    age_rating = Column(String(50))

    # JSONB en Postgres → JSON en SQLAlchemy (el dialecto lo mapea a JSONB)
    system_requirements = Column(JSON)

    # Columna se llama EXACTAMENTE "metadata" en el esquema oficial.
    # No usamos el atributo .metadata de SQLAlchemy, por eso la exponemos
    # como game_metadata pero en BD se llama "metadata".
    game_metadata = Column("metadata", JSON)

    average_rating = Column(Numeric(3, 2), nullable=False, default=0.0, index=True)
    review_count = Column(Integer, nullable=False, default=0)
    total_playtime = Column(BigInteger, nullable=False, default=0)
    download_count = Column(Integer, nullable=False, default=0)

    # TIMESTAMPTZ → DateTime(timezone=True)
    created_at = Column(DateTime(timezone=True), default=datetime.utcnow)
    updated_at = Column(
        DateTime(timezone=True),
        default=datetime.utcnow,
        onupdate=datetime.utcnow,
    )


# ==================== SESIONES ====================

def get_db():
    """
    Dependency de FastAPI para obtener una sesión de BD.

    NO crea tablas. Asume que el esquema ya existe porque lo crearon
    las migraciones oficiales de Postgres.
    """
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
