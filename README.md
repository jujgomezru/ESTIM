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

## 🏗️ Architecture

The official architecture of the project will be expanded as the project gets bigger and more polished.  
Below is the current simplified **project structure diagram**:

<pre>
Final-Project/
│
├── apps/
│   └── java-backend/
│   └── python-backend/
├── db /
│   └── migrations/
│   └── seeds/
│   └── smoke/
├── web/
├── workers/    
├── workshops/
│   └── Workshop-1/
│       ├── Workshop-1.pdf
│       └── README.md
│   └── Workshop-2/
│       └── Workshop-2.pdf
│       └── README.md
│
└── README.md
</pre>


> 📁 *The structure will grow as new modules (e.g., backend, frontend, database) are added.*

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
docker compose up -
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

### 🧨 Troubleshooting
### 1) Java backend starts but React shows “down”

Cause: CORS or wrong env variables
Fix:

- Confirm ```/health``` works in browser

- Confirm ```.env.development.local``` exists

- Check browser devtools > Network > CORS errors

#### 2) Python backend fails with ModuleNotFoundError: estim_py_api

Cause: wrong working directory
Fix:

- Always use ```cd apps/python-backend```

- Or rely on: ```
.venv/bin/python -m uvicorn --app-dir src estim_py_api.main:app```

#### 3) Uvicorn “not found”

Cause: venv not created
Fix:
```bash
cd apps/python-backend
python3 -m venv .venv
. .venv/bin/activate
pip install uvicorn fastapi
```

#### 4) Java backend crashes with “Failed to configure a DataSource”

Cause: JPA tries to auto-load a DB

Fix: DB auto-config is disabled in ```application.yml```:

```bash
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
      - org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
```

#### 5) Docker build fails on Python API

Cause: ```invalid COPY pyproject.toml*```

Fix: use the simplified Dockerfile included in the project.

### 🧭 Developer Workflow

- Start Java backend (make java)

- Start Python backend (make py)

- Start frontend (make web)

- Confirm all health endpoints

- Start building features

### 🤝 Contributing

Keep code inside correct layers (controllers, services, repos)

Use consistent package naming (com.estim.javaapi.*, estim_py_api.*)

Place shared schemas in ```/shared```

Keep Docker and Makefile commands in sync when moving folders

## 🎉 Final Notes

If you follow this README exactly, you will reproduce the same environment as the primary developer — same folder structure, same commands, same output, same behavior.

For any issues, check:

Browser devtools

Terminal logs

Health endpoints

…and if something still doesn’t match, ask for help!


## 👥 Collaborators

- 🥷🏼 **[Jeisson Duván Bareño Ruiz](https://github.com/Jeissonerreape)**
- 👑 **[Juan Jeronimo Gomez Rubiano](https://github.com/jujgomezru)** 
- ✍️ **[Jacobo Alzate Corredor](https://github.com/jalzateco)** 
- 🏥 **[John Jairo Paez Albino](https://github.com/john00-dev)** 

---

🧠 *Developed as part of a software engineering academic project.*
