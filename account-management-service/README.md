# Account Management Service (SCAFFOLD-SVC-001)

Backend scaffold for the Account Management Service, implementing **SCAFFOLD-SVC-001 / AC-1**
on the plan-of-record stack: Java 21, Spring Boot 3.3 (Web, Data JPA, Security, Actuator),
PostgreSQL, Hibernate/JPA, Flyway, Docker.

Scope is limited to scaffolding: project skeleton, environment-based configuration, a
health-check endpoint, and a PostgreSQL datasource with Flyway migrations on startup. No domain
behaviour (authentication, account registration, OAuth 2.0 flows, token issuance) is implemented.

## Configuration

All settings come from environment variables. Secrets and endpoints have no defaults; the
service fails to start if they are missing.

| Variable                   | Required | Default | Purpose                              |
|----------------------------|----------|---------|--------------------------------------|
| `DB_URL`                   | yes      | –       | JDBC URL, e.g. `jdbc:postgresql://host:5432/db` |
| `DB_USERNAME`              | yes      | –       | Database user                        |
| `DB_PASSWORD`              | yes      | –       | Database password                    |
| `SERVER_PORT`              | no       | `8080`  | HTTP port                            |
| `DB_POOL_MAX_SIZE`         | no       | `10`    | Hikari maximum pool size             |
| `DB_CONNECTION_TIMEOUT_MS` | no       | `30000` | Hikari connection timeout            |

See `.env.example` for a template (never commit a populated `.env`).

## Health check

`GET /actuator/health` (plus `/actuator/health/liveness` and `/actuator/health/readiness`) is the
only public endpoint. It includes database connectivity and does not expose details. Every other
request is denied (`403`) until authentication/authorization is specified by a later story.

## Database

The datasource is Spring Boot's auto-configured Hikari pool, bound to `DB_URL`, `DB_USERNAME`,
`DB_PASSWORD` in `src/main/resources/application.yml`. Flyway runs `src/main/resources/db/migration`
on startup; `V1__baseline.sql` creates no domain tables. Hibernate runs with `ddl-auto: validate`.

## Build, lint, test

```bash
mvn spotless:apply   # format (google-java-format)
mvn verify           # unit tests, Testcontainers PostgreSQL ITs (needs Docker), spotless:check
```

## Run locally

```bash
# Against an existing PostgreSQL
DB_URL=jdbc:postgresql://localhost:5432/account_management DB_USERNAME=... DB_PASSWORD=... \
  mvn spring-boot:run
curl localhost:8080/actuator/health   # {"status":"UP",...}

# With Docker Compose (PostgreSQL + service)
cp .env.example .env   # fill in DB_USERNAME / DB_PASSWORD
docker compose up --build
```

## Open gaps

- **Stack discrepancy (unresolved):** the AC-1 text describes a Node.js / Express.js / OAuth 2.0 /
  JWT stack, while the implementation plan and constraints specify Java/Kotlin + Spring Boot.
  This scaffold follows the plan (Java + Spring Boot). Needs a human decision / spec revision.
- **Domain responsibilities out of scope:** AC-1 lists user authentication/authorization, account
  registration/management, OAuth 2.0 flow management, and token generation/validation. None are
  implemented here; no API contracts for them exist in the spec export.
- **Architecture artifacts:** AC-1 references this service's C3/C4 diagrams and API spec "on file
  in Architecture". They are not present in this repository, so nothing was derived from them.
- **HTTPS:** the plan lists REST over HTTPS; TLS termination is not configured in the scaffold
  (no certificate/termination point is specified).
