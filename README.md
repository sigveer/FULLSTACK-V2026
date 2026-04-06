
![Vera Logo](frontend/src/assets/vera.png)

Semester-prosjekt IDATT2105 – Fullstack-appsutvikling (2026)

Et digitalt internkontrollsystem for restauranter, barer og serveringssteder. Vera hjelper virksomheter å forenkle compliance med helse-, sikkerhets- og alkoholreguleringer ved å digitalisere sjekklister, temperaturlogging og rutiner for avviksoppfølging.

## Rask oppstart

### Forutsetninger
- Docker Desktop (inkludert Docker Compose)
- Git

### Kjør appen lokalt

Fra repo-roten:

```bash
docker compose up --build
```

Applikasjonen vil starte på:
- **Frontend**: http://localhost (Nginx + Vue app)
- **Backend API (mat)**: http://localhost:8081
- **Backend API (alkohol)**: http://localhost:8082
- **Database**: mysql:3306

### Stopp appen

```bash
docker compose down
```

Fjern alle data (inkludert databasen):

```bash
docker compose down -v
```

## Testbrukere

Default testdata er satt opp automatisk ved oppstart. Bruk disse credentials for å logge inn:

| Email | Passord | Rolle | Organisasjon |
|-------|---------|-------|--------------|
| `admin@everest.local` | `password` | ADMIN | Everest Sushi & Fusion AS |
| `manager@everest.local` | `password` | MANAGER | Everest Sushi & Fusion AS |
| `employee@everest.local` | `password` | EMPLOYEE | Everest Sushi & Fusion AS & Demo Organization |

## Prosjektstruktur

```
FULLSTACK-V2026/
├── backend/           # Spring Boot API (Java 21+)
│   ├── vera-common/           # Felles tjenester
│   ├── vera-food-service/     # Vera Mat modul
│   └── vera-alcohol-service/  # Vera Alkohol modul
├── frontend/          # Vue.js 3 + Vite
├── docker-compose.yml # Container orchestration
└── docs/              # Dokumentasjon
```

## Tech Stack

| Område | Teknologi | Versjon |
|--------|-----------|---------|
| **Frontend** | Vue.js | 3.x |
| | Vite | 7.x |
| | TypeScript | 5.x |
| | Node.js | 18+ |
| **Backend** | Java | 21 |
| | Kotlin | 2.1+ |
| | Spring Boot | 3.x |
| | Spring Framework | 6.x |
| | Spring Security | 6.x |
| | JPA/Hibernate | Latest |
| **Database** | MySQL | 8.4 |
| | Flyway | Latest |
| **DevOps** | Docker | Latest |
| | Docker Compose | Latest |
| **API** | Swagger/OpenAPI | 3.0 |
| **Testing** | JUnit 5 | Latest |
| | Jest/Vitest | Latest |

## Database

- **Database**: `vera_system`
- **Bruker**: `vera_user`
- **Passord**: `vera_password`
- **Port**: 3306

Backend bruker **Flyway** for databasemigrasjoner og oppretter/migrerer schema automatisk ved oppstart.

## Dokumentasjon

- **API referanse**: Se Swagger UI på `http://localhost/swagger.html`
- **Systemdokumentasjon**: Se `/docs/` for arkitektur, klassediagrammer og setup-guide
- **Wiki**: Full teknisk dokumentasjon og arkitektur-notater

## Nyttige kommandoer

**Se logger fra alle tjenester:**
```bash
docker compose logs -f
```

**Se logger fra bare backend:**
```bash
docker compose logs -f vera-mat vera-alkohol
```

**Restart bare backend (for rask utvikling):**
```bash
docker compose restart vera-mat vera-alkohol
```

**Kjør bare backend lokalt (uten Docker):**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Kjør bare frontend lokalt (uten Docker):**
```bash
cd frontend
npm run dev
```
---
