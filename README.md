# Backend Template

A modern Spring Boot application template with JWT authentication, database integration, and REST API capabilities.

## Tech Stack

- **Language**: Kotlin 2.3.20
- **Framework**: Spring Boot 4.0.4
- **Build Tool**: Gradle with Kotlin DSL
- **Database**: PostgreSQL
- **Migration Tool**: Liquibase
- **Authentication**: Spring Security OAuth2 Resource Server (JWT)
- **JVM**: Java 25

## Features

- OAuth2/OIDC JWT authentication ready
- PostgreSQL database integration
- Liquibase database migrations
- REST API with Spring Web MVC
- Kotlin serialization support
- Structured logging with kotlin-logging
- Testing setup with JUnit 5
- Code quality with ktlint

## Prerequisites

- JDK 25 (or compatible version)
- Docker (for PostgreSQL)
- Gradle (wrapper included)

## Getting Started

### 1. Start PostgreSQL Database

```bash
docker-compose up -d
```

This will start a PostgreSQL container with the following credentials:
- Host: localhost:5432
- Database: db
- Username: postgres
- Password: password

### 2. Configure Authentication

Update `src/main/resources/application.yaml` with your JWT issuer URI:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: ${JWT_ISSUER_URI:https://your-auth-provider.example.com}
```

You can set the `JWT_ISSUER_URI` environment variable or update the default value.

### 3. Build the Project

```bash
./gradlew build
```

### 4. Run the Application

```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`.

## Project Structure

```
src/
├── main/
│   ├── kotlin/
│   │   └── org/kotletai/backend/
│   │       ├── config/          # Configuration classes (SecurityConfig, etc.)
│   │       ├── controller/      # REST controllers
│   │       ├── model/           # Domain models
│   │       ├── repository/      # JPA repositories
│   │       └── service/         # Business logic
│   └── resources/
│       ├── application.yaml     # Application configuration
│       └── db/changelog/        # Liquibase migrations
└── test/
    └── kotlin/                  # Test files
```

## Available Gradle Tasks

- `./gradlew build` - Build the project
- `./gradlew test` - Run tests
- `./gradlew bootRun` - Run the application
- `./gradlew ktlintCheck` - Check code style
- `./gradlew ktlintFormat` - Format code

## Customization

This is a template project. Feel free to:

1. Update package names from `org.kotletai.backend` to your own
2. Modify the `group` and `description` in `build.gradle.kts`
3. Add your own controllers, services, and models
4. Configure additional dependencies in `gradle/libs.versions.toml`

## Database Migrations

Database migrations are managed with Liquibase. Add new changesets in:

```
src/main/resources/db/changelog/
```

## Security

The application is configured to use JWT tokens for authentication. Ensure you:

1. Configure a valid JWT issuer URI
2. Set up proper CORS configuration for your frontend
3. Review and adjust security settings in `SecurityConfig.kt`

## License

This template is free to use for any project.
