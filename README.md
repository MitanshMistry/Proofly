# Proofly

Proofly is a proof-of-purchase and warranty vault built as a full-stack Java/Spring Boot and React/TypeScript application.

## Stack

- Java 21 and Spring Boot REST
- PostgreSQL with Flyway SQL migrations
- React, TypeScript, and Vite
- Maven and Docker Compose for local development

## Project layout

```text
backend/     Spring Boot API and database migrations
frontend/    React/TypeScript web application
docker-compose.yml
```

## Run locally

1. Recommended PostgreSQL mode: `docker compose up -d postgres`, then `cd backend; mvn spring-boot:run`
2. Docker-free runtime fallback: `cd backend; mvn spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=local'`
3. Start the web app: `cd frontend; npm.cmd install; npm.cmd run dev`

The API runs at `http://localhost:8080` and the web app at `http://localhost:5173`.

The current implementation includes:

- Spring Security session authentication and password-hashed accounts.
- Owned receipt CRUD, newest-first retrieval, CSV export, and deletion.
- Browser Tesseract OCR adapter with manual review and local image storage.
- Merchant aggregation, projects, project receipt assignment, and derived history.
- A responsive React dashboard with search, sort, filtering, upload, organization, and settings views.
- PostgreSQL/Flyway, Docker Compose, H2 local fallback, Dockerfiles, and GitHub Actions CI.

## Zero-cost status

The complete local-first product loop has no required paid service:

- Use H2 with `mvn spring-boot:run '-Dspring-boot.run.arguments=--spring.profiles.active=local'`.
- Run the React app with `npm.cmd run dev`.
- Use browser Tesseract.js for OCR.
- Use **Continue as demo** on the login screen to enter the local H2 app without creating an account. This endpoint exists only with the `local` Spring profile.
- Store uploaded receipt images in a local filesystem adapter.
- Use the included PostgreSQL Compose file for the production-style database when Docker is available.
- Use GitHub Actions free repository runners for CI.

The local implementation does not require Azure, AWS, managed OCR, paid storage, paid email, or paid database hosting.

Environment-limited items:

- Docker/Compose startup could not be verified because Docker is not installed in this machine.
- PostgreSQL startup could not be verified until Docker or a local PostgreSQL instance is available.
- Browser OCR accuracy should be validated with real receipt images.
- Password reset requires a secure email provider and is not implemented as an insecure demo.

## What is needed now

- Java/Spring Boot: the production API and business rules.
- PostgreSQL/SQL: durable receipt, merchant, project, and history data.
- React/TypeScript: the responsive product experience.
- Docker: only to run PostgreSQL consistently on a developer machine.

Git is assumed for source history, but CI is not required until the first vertical slice is stable. Azure is a deployment decision for later; it should not slow down local product development.
