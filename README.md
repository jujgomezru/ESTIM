# 🎮 ESTIM — Digital Game Store Parody

> **ESTIM** (Entertainment Software Totally Imitating Markets) is a parody of the Steam platform.  
> It is a simple web application where users can browse, purchase, and review digital games through a mock storefront.

---

## 🧩 Project Overview

**ESTIM** simulates a digital distribution platform for video games.  
Users can create accounts, browse games, add them to a shopping cart, complete mock purchases, and post reviews.  
Distributors can publish games and access basic sales statistics.

This project was developed for educational purposes to demonstrate:
- Object-Oriented Programming principles (OOP)
- Use of at least **two design patterns** (e.g., Singleton and Factory)
- Integration with a **relational database** (SQLite)
- RESTful API design and automated testing
- Basic CI/CD pipeline with **GitHub Actions** and **Docker**

---

## 🏗️ Architecture Overview

ESTIM follows a layered hexagonal-influenced architecture, split across the web, java-backend, and python-backend services.
Each service mirrors the same clean structure.

The official architecture of the project will be expanded as the project gets bigger and more polished.  
Below is the current simplified **project structure diagram**:

<pre>
Final-Project/
│
├── apps/
│   ├── java-backend/       → Spring Boot service (auth, users, library)
│   └── python-backend/     → FastAPI service (misc endpoints)
│
├── web/                    → React+Vite frontend
│
├── db/
│   ├── migrations/         → Database version control
│   ├── seeds/              → Initial data
│   └── smoke/              → Smoke tests
│
├── workers/                → Event workers (future)
└── README.md
</pre>


> 📁 *The structure will grow as new modules (e.g., backend, frontend, database) are added.*

### 1️⃣ Presentation Layer — (User → Routes → API Clients → Controllers)

Purpose: Convert HTTP requests into application commands.

Includes:

- React frontend (`web/`)

- Pages

- API routes

- API client wrappers for Java & Python services

- Java controllers (`apps/java-backend/controllers`)

- Python controllers (`apps/python-backend`)

The Presentation Layer never contains business logic.
Its job is to:

- Validate input

- Build DTOs / Requests

- Call the appropriate Service

- Return an HTTP response

### 2️⃣ Business Layer — (Services, Workflows, Domain Models)

Purpose: Implement the core application logic.

Includes (per backend):

- Application services:

- Authentication

- Library handling

- Payment methods

- Game workflows

- Domain models: User, Game, Review, LibraryEntry, etc.

- Domain events (event bus planned but not fully implemented)

This layer:

- Has no idea about controllers or HTTP

- Speaks only in domain entities

- Sends domain events when important things occur
(e.g., GamePurchased, UserRegistered)

### 3️⃣ Event Handlers Layer — (Async, Side Effects)

Purpose: React to domain events and perform indirect/secondary actions.

Each backend has its own event handlers (i.e. `apps/java/backend/application/handlers`).

Example responsibilities (future):

- Send emails

- Update search indexes

- Run background workers

- Sync stats

### 4️⃣ Data Access Layer — (Repositories, DAOs, Mappers)

Purpose: Convert domain entities ⇄ database rows.

Contains:

- Repositories (Java + Python)

- ORM/JPA entities or SQL DAOs

- Row-to-domain mappers

- Database adapters

This layer isolates the Business Layer from the database.
The Business Layer never sees SQL or ORM code.

### 5️⃣ Data Layer — (Postgres, Seeds, Migrations)

Purpose: Persist everything.

- Primary database → PostgreSQL dockerized DB via docker-compose

- Migrations and seeds in `/db`

Redis/Cache layer not implemented yet

---

## 🚀 ESTIM — Development Environment Guide  
  
This section explains everything a new developer needs to get the environment running exactly as intended: folder structure, setup steps, commands, troubleshooting, and expectations.

## 🛠️ Prerequisites

Install the following:

### **Required**
- **Node.js ≥ 20**  
  Check with: `node -v`
- **npm ≥ 9**  
  Check with: `npm -v`
- **Java 21 (OpenJDK / Temurin)**  
  Check with: `java -version`
- **Python 3.10+ (3.12/3.13 OK)**  
  Check with: `python3 --version`
- **make**  
  Check with: `make -v`
- **Docker & Docker Compose**  
  Check with:  
  - `docker -v`  
  - `docker compose version`

If something is missing, install it before proceeding.

---

## 📦 First-Time Setup

Run these steps once after cloning the repository.

---

### 1) Install frontend dependencies

```bash
cd web
npm install
cd ..
```

### 2) Create Python virtual environment
```bash
cd apps/python-backend
python3 -m venv .venv
. .venv/bin/activate
pip install --upgrade pip
pip install fastapi uvicorn
cd ../../..
```


The project uses .venv/bin/python -m uvicorn, so no need to activate the venv for normal development.

## ▶️ Running the Project (with Makefile)

From the project root, run:

**Frontend**

```bash
make web
```


Starts Vite dev server on:
👉 http://localhost:5173

**Java Backend**
```bash
make java
```


Starts Spring Boot dev server on:
👉 http://localhost:8080/health

The window stays open because Spring Boot runs continuously.
Press CTRL+C to stop it.

**Python Backend**
```bash
make py
```


Uses virtualenv automatically (no manual activation needed).

Starts FastAPI dev server on:
👉 http://localhost:8000/health

Press CTRL+C to stop it.

## ▶️ Running Everything with Docker

If you prefer containers:

```bash
docker compose build
docker compose up -d
```


Then check:

Frontend: http://localhost:5173

Java API: http://localhost:8080/health

Python API: http://localhost:8000/health

Stop with:

```bash
docker compose down
```

🔥 Health 

**Java** 

GET http://localhost:8080/health


Returns plain text, e.g.:

java-api:ok

**Python backend**

GET http://localhost:8000/health


Returns JSON:

{
  "status": "py-api:ok"
}

**Frontend (React)**

The homepage shows both backend statuses.

**🧬 Environment Variables (Frontend)**

The React app expects:

```bash
VITE_JAVA_API_BASE=http://localhost:8080
VITE_PY_API_BASE=http://localhost:8000
```


Create web/.env.development.local:

```bash
cd web
echo "VITE_JAVA_API_BASE=http://localhost:8080" >> .env.development.local
echo "VITE_PY_API_BASE=http://localhost:8000" >> .env.development.local
```


Restart Vite after editing env files.

### 🔐 CORS Configuration
**Python backend**

Already supports CORS for http://localhost:5173.

**Java backend**

Controller is annotated with:

```bash
@CrossOrigin(origins = "http://localhost:5173")
```


Or global CORS config is defined in /config.

If calling Java API fails from the browser but works with curl, it is always a CORS issue.

### 🧰 Developer Commands Summary
| Task               | Command                | Notes                      |
| ------------------ | ---------------------- | -------------------------- |
| Run React frontend | `make web`             | Port **5173**              |
| Run Java backend   | `make java`            | Port **8080**, Spring Boot |
| Run Python backend | `make py`              | Port **8000**, FastAPI     |
| Stop everything    | `CTRL+C`               | Per-process                |
| Start via Docker   | `docker compose up -d` | All services               |
| Rebuild containers | `docker compose build` | After code changes         |
| Stop Docker stack  | `docker compose down`  |                            |


## 📹 Video

[![YouTube Thumbnail](https://img.youtube.com/vi/QlC2CWs5sQE/maxresdefault.jpg)](https://www.youtube.com/watch?v=QlC2CWs5sQE)

## 👥 Collaborators

- 🥷🏼 **[Jeisson Duván Bareño Ruiz](https://github.com/Jeissonerreape)**
- 👑 **[Juan Jeronimo Gomez Rubiano](https://github.com/jujgomezru)** 
- ✍️ **[Jacobo Alzate Corredor](https://github.com/jalzateco)** 
- 🏥 **[John Jairo Paez Albino](https://github.com/john00-dev)** 

---

🧠 *Developed as part of a software engineering academic project.*
#   T r i g g e r   w o r k f l o w  
 