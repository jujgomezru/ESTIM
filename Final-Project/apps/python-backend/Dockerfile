FROM python:3.12-slim

ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1

WORKDIR /app

# Optional build tools (handy later for psycopg/uvloop/etc.)
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential && \
    rm -rf /var/lib/apt/lists/*

# Install runtime deps (we’re not relying on pyproject here)
RUN pip install --no-cache-dir --upgrade pip && \
    pip install --no-cache-dir fastapi uvicorn

# Copy source (your package is under src/)
COPY src /app/src

EXPOSE 8000
CMD ["uvicorn", "--app-dir", "src", "estim_py_api.main:app", "--host", "0.0.0.0", "--port", "8000"]
