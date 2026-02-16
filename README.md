# Digital Fixed Deposit System

A full-stack, production-grade web application for managing digital fixed deposits (FDs), designed with robust security, clean architecture, and comprehensive test coverage. This capstone project showcases end-to-end engineering: domain modeling, REST APIs, authentication/authorization, lifecycle workflows, admin operations, notifications, and modern frontend UX.

## Table of Contents
- Overview
- Architecture
- Tech Stack
- Features
- Domain Model & Lifecycles
- Security & Compliance
- Design Principles & Patterns
- Frontend (Vue 3 + Vite)
- Backend (Spring Boot)
- API Overview
- Configuration & Environment
- Local Development
- Docker & Deployment
- Testing & Quality
- Logging & Observability
- Project Structure
- Contribution Guidelines
- Roadmap & Future Work
- License

## Overview
The Digital Fixed Deposit System enables users to create, manage, and withdraw fixed deposits online while admins oversee approvals, interest calculations, and support tickets. The app demonstrates:
- Real-world financial flows: FD creation, maturity, premature withdrawal, penalties.
- Role-based access: Admin vs User shells and feature sets.
- Secure auth with JWT and optional Google OAuth.
- Notification center with confirmations.
- Modular frontend store/services and clean backend layers.

## Architecture
- Two services: `backend` (Spring Boot REST API) and `frontend` (Vue 3 SPA), deployed with Nginx.
- PostgreSQL database, orchestrated via Docker Compose.
- Networked services on a bridge network (`fd-network`) with environment-driven configuration.
- Logging via Spring logback.

High-level diagrams (see `docs/`):
- FD System Architecture.png
- Class Diagram.png
- FD Lifecycle - State Diagram.png
- Support Ticket Lifecycle - State Diagram.png
- Auth Flow Sequence Diagram.png

## Tech Stack
- Frontend: Vue 3, Vite, TypeScript, Vue Router, Vuex, Axios, SCSS; Unit tests with Vitest and Jest + Vue Test Utils.
- Backend: Spring Boot (webmvc, validation, security, data-jpa, mail, OAuth2 client), JJWT for tokens; tests with Spring Boot Starter Test, H2, WebMvc/Data JPA test; coverage via JaCoCo.
- Database: PostgreSQL 16.
- Containerization: Docker multi-stage builds; orchestration via Docker Compose.

## Features
User-facing:
- Sign up / Sign in (JWT) with optional Google OAuth.
- Create FD with principal, tenure, interest rate.
- View FD details, status, maturity date, accrued interest.
- Request premature withdrawal (penalty handling) or withdrawal at maturity.
- Notification center and confirmation boxes for critical actions.
- Profile management, basic settings.

Admin-facing:
- Admin dashboard: overview of FDs, pending approvals, withdrawals.
- Manage FD products, interest rates, policies.
- Review and resolve support tickets.
- System monitoring via logs.

Cross-cutting:
- Role-based shells: body classes `admin-shell` and `user-shell` toggle UX.
- Validation and error handling across layers.
- Centralized Axios instance with interceptors.

## Domain Model & Lifecycles
- Fixed Deposit Lifecycle: Draft → Active → Matured → Withdrawn; Premature Withdrawal path includes penalty application (see `docs/FD Lifecycle - State Diagram.png`).
- Support Ticket Lifecycle: Open → In Progress → Resolved/Closed; includes reassignment/escalation paths (see `docs/Support Ticket Lifecycle - State Diagram.png`).
- Auth flow: Router guards, JWT-based session, and Google OAuth callback (see `docs/Auth Flow Sequence Diagram.png`).

## Security & Compliance
- Spring Security: Role-based authorization for `ADMIN` and `USER`.
- JWT (JJWT) with configurable secret and expiration.
- OAuth2 (Google) with secure redirect and scopes (openid, profile, email).
- Sensitive configuration via environment variables in containerized environments.
- Validation: Bean validation (`spring-boot-starter-validation`) for request DTOs.
- Mail delivery via Spring Mail with TLS enabled.
- Audit-friendly logs via logback.

Note: Do not commit real secrets. Use environment variables or secret managers in production.

## Design Principles & Patterns
The project emphasizes:
- Clean Architecture and Layered Design: Controllers → Services → Repositories; DTOs for API boundaries.
- Domain-Driven Design (DDD) elements: clear aggregates (FD, User, SupportTicket), lifecycle states.
- SOLID Principles:
  - Single Responsibility: Components (e.g., `authServices.ts`, `fdService.ts`) handle one concern.
  - Open/Closed: New routes/views/features can be added without modifying core abstractions.
  - Liskov Substitution: Interface-driven services and mocks for tests.
  - Interface Segregation: Fine-grained Vuex modules and service APIs.
  - Dependency Inversion: Controllers depend on service interfaces; frontend depends on Axios wrapper, not raw requests.
- Separation of Concerns: Auth handled by router guards; UI shells toggled in `App.vue` via route.
- DRY & Reusability: Common components (`Navbar`, `Footer`, `ConfirmNotificationBox`, `NotificationCenter`).
- Defensive Programming: Validators, central error handling, typed composables.
- Testability: Decoupled services and store make unit testing straightforward.

Frontend-specific patterns:
- Composition API with composables (`useTheme`, `useNotifications`).
- Centralized Axios instance with interceptors for auth and errors.
- Vuex store modules: `auth`, `fd`, `support`.
- Router guards for auth and role access.

Backend-specific patterns:
- Spring MVC controllers, service layer, repository layer.
- Method-level validation and exception handling.
- JPA entities with Lombok to reduce boilerplate.
- DTOs and mappers for request/response segregation.

## Frontend (Vue 3 + Vite)
Key files:
- `src/App.vue`: Synchs shell classes based on route and renders `ConfirmNotificationBox`.
- `src/router/index.ts`: Defines routes for user/admin flows and guards.
- `src/store/`: Vuex store and modules (`auth.ts`, `fd.ts`, `support.ts`).
- `src/services/`: API services (`authServices.ts`, `fdService.ts`, `adminService.ts`, `withdrawalService.ts`, `supportService.ts`) and `axios.ts` setup.
- `src/components/common/`: Common UI components (Navbar, Footer, Notifications).
- `src/views/`: Pages for Home, Profile, Admin dashboards, legal pages.
- `tests/`: Vitest + Jest-based unit tests across services, components, utilities, router, store.

Scripts:
- `npm run dev`: Vite dev server.
- `npm run build`: Type-check (`vue-tsc -b`) and build.
- `npm run preview`: Serve built assets.
- `npm run test`: Vitest.
- `npm run test:jest`: Jest tests.
- `npm run coverage`: Vitest coverage.

Docker:
- Multi-stage build with Node 20; Nginx serves static assets.
- `nginx.conf` configures routing and SPA fallback.

## Backend (Spring Boot)
Dependencies (see `backend/pom.xml`):
- Web: `spring-boot-starter-webmvc`, validation, mail.
- Data: `spring-boot-starter-data-jpa`, PostgreSQL driver.
- Security: `spring-boot-starter-security`, OAuth2 client, JJWT.
- Testing: Boot starter test, H2, webmvc/data-jpa-test; JaCoCo for coverage.

Configuration:
- `application.properties`: datasource (local dev), JPA options, JWT config, mail settings, Google OAuth provider/registration.
- `application-docker.yml`: container-friendly overrides (see Docker Compose environment variables).
- `logback-spring.xml`: log formatting and appenders.

Build & Run:
- Maven Wrapper (`mvnw`) included.
- Dockerfile: Multi-stage build (Maven + Temurin JRE). The runtime image exposes port 8080 and runs `app.jar`.

## API Overview
Typical endpoints (based on services and store modules; exact paths in controllers):
- Auth:
  - POST `/api/auth/login` (JWT issuance)
  - POST `/api/auth/register`
  - GET `/oauth2/authorization/google` (redirect to Google)
  - GET `/login/oauth2/code/google` (OAuth callback)
- User FDs:
  - GET `/api/fd` (list user FDs)
  - POST `/api/fd` (create FD)
  - GET `/api/fd/{id}` (details)
  - POST `/api/fd/{id}/withdraw` (maturity or premature)
- Admin:
  - GET `/api/admin/fd` (list all / pending)
  - PATCH `/api/admin/fd/{id}` (approve/reject/update)
  - GET `/api/admin/support` (tickets)
  - PATCH `/api/admin/support/{id}` (resolve/close)
- Support:
  - POST `/api/support` (create ticket)
  - GET `/api/support` (list user tickets)

Note: Paths are illustrative; check controllers for exact mappings.

## Configuration & Environment
Local dev (`backend/src/main/resources/application.properties`) uses local Postgres defaults:
- `spring.datasource.url=jdbc:postgresql://localhost:5432/Digital_Fixed_Deposit_System_DB`
- `spring.datasource.username=postgres`
- `spring.datasource.password=123456`
- `security.jwt.secret` and `security.jwt.expiration-ms` configure JWT.
- `spring.mail.*` configure SMTP (TLS) — use env vars in production.
- Google OAuth registration/provider fields.

Containerized env (see `docker-compose.yml`):
- `db` service uses `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD`.
- `backend` uses `SPRING_PROFILES_ACTIVE`, database credentials, `JWT_SECRET`, `GOOGLE_CLIENT_ID/SECRET/REDIRECT_URI`.
- `frontend` served via Nginx on port 3000.

## Local Development
Prerequisites: Node 20+, npm, Java 17, Maven (wrapper included), PostgreSQL.

Frontend:
- Install deps: `npm install` (in `frontend/`).
- Run dev: `npm run dev`.
- Run tests: `npm run test` or `npm run test:jest`.

Backend:
- Configure local DB and update `application.properties` as needed.
- Build: `./mvnw clean package` (in `backend/`).
- Run: `java -jar target/*.jar` or via IDE.
- Tests: `./mvnw test`.

## Docker & Deployment
Build images:
- Backend: multi-stage Maven build producing `digital-fd-backend:1.0` (see Dockerfile).
- Frontend: Node build → Nginx serving built assets → `digital-fd-frontend:1.0`.

Compose up:
1. Ensure an `.env` file with variables:
   - `DB_NAME`, `DB_USER`, `DB_PASSWORD`
   - `SPRING_PROFILE`
   - `JWT_SECRET`
   - `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `GOOGLE_REDIRECT_URI`
2. Run: `docker compose up -d`

Services:
- Postgres: `localhost:5433` (mapped from container `5432`).
- Backend: `localhost:8080`.
- Frontend: `localhost:3000`.

## Testing & Quality
Frontend:
- Vitest (`npm run test`, `npm run coverage`) with `happy-dom` and `@testing-library/vue`.
- Jest (`npm run test:jest`) for selected suites.
- Coverage via Vitest; types enforced via TypeScript and `vue-tsc` during build.

Backend:
- Spring Boot tests with H2 for isolation.
- Web MVC and Data JPA test starters configured.
- Coverage via JaCoCo plugin in Maven.

CI-ready Tasks:
- Frontend: typecheck + build + tests.
- Backend: `mvn -B verify` including tests and coverage.

## Logging & Observability
- `logback-spring.xml` configures structured logging.
- Runtime logs written under `backend/logs/Digital_Fixed_Deposit_System.log` (rotations configured in logback).
- Application-level events for key lifecycle operations.

## Project Structure
```
Digital-Fixed-Deposit-System/
  docker-compose.yml
  backend/
    Dockerfile
    pom.xml
    src/main/java/tech/... (controllers, services, repositories, entities)
    src/main/resources/
      application.properties
      application-docker.yml
      logback-spring.xml
    logs/Digital_Fixed_Deposit_System.log
  frontend/
    Dockerfile
    package.json
    src/
      App.vue
      router/
      store/
      services/
      components/
      views/
    tests/
      unit suites and setup
  docs/
    Architecture and lifecycle diagrams
```

## Contribution Guidelines
- Create feature branches and open PRs with linked issues.
- Add unit tests for new features (frontend + backend as applicable).
- Keep services and store modules small and cohesive.
- Avoid committing secrets; prefer env vars or secret managers.

## Roadmap & Future Work
- Full integration tests for end-to-end flows.
- Role management UI for admins.
- Advanced audit logging and dashboards.
- i18n and accessibility improvements.
- CI/CD pipelines with container builds and vulnerability scanning.

## License
Educational capstone project. Adapt licensing as needed for deployment.
