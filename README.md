# FULLSTACK-V2026

Lokal kjoring med Docker Compose for issue #4.

## Forutsetninger
- Docker Desktop (med Docker Compose)

## Starter containere
Kjor fra repo-roten:

```bash
docker compose up --build
```

Dette starter:
- `mysql` pa `localhost:3306`
- `backend` (Spring Boot `ik-common`) pa `localhost:8080`
- `frontend` (Vite/Vue) pa `localhost:5173`

## Stopper containere
```bash
docker compose down
```

For a fjerne data-volum (inkludert MySQL-data):

```bash
docker compose down -v
```

## Databasedetaljer (lokalt)
- Database: `ik_system`
- Bruker: `ik_user`
- Passord: `ik_password`

`ik-common` bruker Flyway og vil opprette/migrere schema ved oppstart.

## Nyttige kommandoer
Se logger:

```bash
docker compose logs -f
```

Restart kun backend:

```bash
docker compose restart backend
```
