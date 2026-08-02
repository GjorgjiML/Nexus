# Nexus

Nexus is a small social media platform built with a pure Java stack: **Java 21**, **Spring Boot 3.3**, **Spring Security + JWT**, **Spring Data JPA**, **PostgreSQL**, **Flyway**, and **Thymeleaf + Bootstrap 5**.

## Features

- Register / login / logout (stateless JWT stored in an HttpOnly cookie)
- User profiles (bio + avatar), follow / unfollow, followers & following lists
- Posts with optional image upload
- Home feed (own posts + followed users, newest first)
- Like / unlike posts
- Comments (add + delete own)
- Dark / light theme toggle
- Springdoc OpenAPI UI at `/swagger-ui.html`

## Quick start (Docker Compose)

**Requirements:** Docker and Docker Compose.

From the project root:

```bash
docker compose up --build -d
```

Wait until the app is healthy, then open **http://localhost:8080**

Useful commands:

```bash
# Follow logs
docker compose logs -f app

# Stop
docker compose down

# Stop and wipe database + uploads
docker compose down -v
```

Compose starts two services:

| Service | Container     | Port  | Purpose                          |
|---------|---------------|-------|----------------------------------|
| `db`    | `nexus-db`    | 5432  | PostgreSQL 16                    |
| `app`   | `nexus-app`   | 8080  | Spring Boot application          |

Flyway runs on startup and seeds demo data.

## Test accounts

All seed users share the password **`password123`**:

| Username | Email           | Password    |
|----------|-----------------|-------------|
| alice    | alice@nexus.dev | password123 |
| bob      | bob@nexus.dev   | password123 |
| carol    | carol@nexus.dev | password123 |
| dave     | dave@nexus.dev  | password123 |
| eve      | eve@nexus.dev   | password123 |

Recommended first login: **alice** / **password123**

## Useful URLs

| URL | Description |
|-----|-------------|
| http://localhost:8080/login | Login |
| http://localhost:8080/register | Register |
| http://localhost:8080/ | Home feed (auth required) |
| http://localhost:8080/profile/alice | Public profile |
| http://localhost:8080/profile/edit | Edit own profile |
| http://localhost:8080/post/create | Create a post |
| http://localhost:8080/swagger-ui.html | OpenAPI UI |

## Run without Docker Compose (optional)

### Requirements

- Java 21+
- Maven 3.9+
- PostgreSQL 16 (Docker or local)

### Start PostgreSQL only

```bash
docker run -d \
  --name nexus-postgres \
  -e POSTGRES_DB=nexus \
  -e POSTGRES_USER=nexus \
  -e POSTGRES_PASSWORD=nexus \
  -p 5432:5432 \
  postgres:16-alpine
```

### Run the app with Maven

```bash
mvn spring-boot:run
```

Or build a jar:

```bash
mvn clean package -DskipTests
java -jar target/nexus-1.0.0.jar
```

## Configuration

Main settings live in `src/main/resources/application.yml` (`dev` profile is active by default).

| Property / env var | Default | Purpose |
|--------------------|---------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/nexus` | DB URL (`db` host in Compose) |
| `SPRING_DATASOURCE_USERNAME` | `nexus` | DB user |
| `SPRING_DATASOURCE_PASSWORD` | `nexus` | DB password |
| `JWT_SECRET` | (dev secret in yml) | JWT HMAC secret |
| `UPLOAD_DIR` | `./uploads` (`/app/uploads` in Compose) | Image upload directory |
| `server.port` | `8080` | HTTP port |

Compose sets these for the `app` service in `docker-compose.yml`.

## Project structure

```
src/main/java/com/nexus/
  config/        Security, Web MVC, OpenAPI
  controller/    Spring MVC controllers
  dto/           Forms + view models
  entity/        JPA entities
  exception/     Global exception handling
  repository/    Spring Data JPA
  security/      JWT, UserDetails, cookies
  service/       Business logic + file storage
src/main/resources/
  db/migration/  Flyway SQL
  templates/     Thymeleaf pages
  static/        CSS / JS
Dockerfile
docker-compose.yml
uploads/         Uploaded images (local / volume)
```

## Tech stack

- Java 21
- Spring Boot 3.3
- Spring Security 6 (JWT, stateless)
- Spring Data JPA + PostgreSQL
- Flyway
- Lombok
- Jakarta Validation
- Springdoc OpenAPI
- Thymeleaf + Bootstrap 5 + vanilla JS
- Docker Compose

## Development notes

- Auth is **JWT in an HttpOnly cookie** (`nexus_token`) so classic form posts work with server-side rendering.
- CSRF is disabled because the session is stateless; protect the JWT secret in production and set `nexus.cookie.secure=true` behind HTTPS.
- Max upload size is **5MB**; allowed types: JPEG, PNG, GIF, WebP.
