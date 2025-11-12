.PHONY: web java py

# --- Frontend (Vite + React)
web:
	cd web && npm run dev

# --- Java backend (Spring Boot)
java:
	cd apps/java-api && ./gradlew bootRun

# --- Python backend (FastAPI)
py:
	cd apps/python-api && \
	. .venv/bin/activate && \
	uvicorn --app-dir src estim_py_api.main:app --reload --port 8000
