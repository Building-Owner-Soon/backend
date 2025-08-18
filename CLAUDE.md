# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

"내 꿈은 건물주" (My Dream is to be a Building Owner) is a Korean financial relationship tracking service that records and shares financial episodes between relationships (friends/lovers/family). Built with Kotlin, Spring WebFlux, and R2DBC for reactive programming.

## Development Commands

### Build and Run
```bash
./gradlew build          # Build project and install git hooks
./gradlew bootRun        # Run the application (port 8000)
```

### Code Quality
```bash
./gradlew ktlintCheck    # Check Kotlin code formatting
./gradlew ktlintFormat   # Auto-fix formatting issues
./gradlew detekt         # Run static code analysis
```

### Testing
```bash
./gradlew test           # Run all tests (uses Kotest + JUnit 5)
./gradlew test --tests "ClassName"  # Run specific test class
```

### Git Hooks
Git hooks are automatically installed during build. They run:
- **Pre-commit**: ktlint and detekt on staged Kotlin files
- **Pre-push**: Full ktlint and detekt checks on entire codebase

## Architecture

### Domain-Driven Design Structure
```
src/main/kotlin/com/bos/backend/
├── application/        # Application services and use cases
│   ├── auth/          # Authentication strategies and services
│   ├── email/         # Email verification services
│   └── service/       # Core business services (JWT, Email)
├── domain/            # Domain entities and repositories
│   ├── auth/          # Auth domain objects
│   ├── term/          # Terms and agreements
│   └── user/          # User entities and enums
├── infrastructure/    # External concerns and implementations
│   ├── config/        # Spring configurations (R2DBC, Redis, Security)
│   ├── external/      # External API integrations (Kakao)
│   ├── persistence/   # Repository implementations
│   └── security/      # Security implementations
└── presentation/      # Controllers and DTOs
    └── auth/          # Authentication endpoints
```

### Key Technologies
- **Spring WebFlux**: Reactive web framework
- **R2DBC**: Reactive database connectivity (MariaDB)
- **Redis**: Caching and session storage
- **JWT**: Authentication tokens
- **Kotest**: Testing framework with StringSpec style
- **Testcontainers**: Integration testing

### Authentication System
Multi-strategy authentication supporting:
- BOS (internal) authentication
- Kakao OAuth integration
- JWT-based token system (6h access, 30d refresh)

## Git Workflow

### Branch Strategy
- `master`: Production environment
- `staging`: QA environment  
- `develop`: Development environment

### Commit Prefixes
- `feature`: New functionality
- `fix`: Bug fixes
- `hotfix`: Critical production fixes
- `chore`: Configuration changes
- `docs`: Documentation
- `style`: Code formatting/style

## Environment Configuration

Application uses environment variables for configuration:
- `db-host`, `db-user`, `db-password`: Database connection
- `jwt-secret`: JWT signing key
- `mail-username`, `mail-password`: SMTP credentials
- `redis-host`, `redis-port`: Redis connection

## Testing Patterns

Tests use Kotest StringSpec with Korean descriptions:
```kotlin
class ServiceTest : StringSpec({
    "메소드는 조건에서 예상결과를 반환한다" {
        // Given-When-Then pattern
        result shouldBe expected
    }
})
```

Integration tests use `@SpringBootTest` with `TestcontainersConfiguration`.