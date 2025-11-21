from sqlalchemy import Column, Integer, String, Float
from database import Base

class Game(Base):
    __tablename__ = "games"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String)
    precio = Column(Float)
