# Java DevOps Project

A demonstration of **Gradle build automation**, **dependency management**, and **CI/CD pipeline integration** for a Java application.

---

## Project Structure

```
java-devops-project/
├── src/
│   ├── main/java/com/devops/app/
│   │   ├── Application.java          # Entry point
│   │   ├── config/AppConfig.java     # Configuration constants
│   │   ├── model/Task.java           # Domain model
│   │   ├── repository/               # Data access layer
│   │   └── service/TaskService.java  # Business logic
│   └── test/java/com/devops/app/
│       ├── service/TaskServiceTest.java
│       └── controller/TaskModelTest.java
├── .github/workflows/ci-cd.yml       # GitHub Actions pipeline
├── docker/
│   ├── Dockerfile                    # Multi-stage Docker build
│   └── docker-compose.yml            # Local container orchestration
├── scripts/
│   ├── build.sh                      # Local build automation
│   └── deploy.sh                     # Docker build/deploy
├── build.gradle                      # Gradle build configuration
└── settings.gradle
```

---

## Prerequisites

| Tool   | Minimum Version |
|--------|----------------|
| Java   | 17             |
| Gradle | 8.x (via wrapper) |
| Docker | 24.x (optional) |

---

## Quick Start

### 1. Clone and build

```bash
git clone https://github.com/your-username/java-devops-project.git
cd java-devops-project
chmod +x gradlew
```

### 2. Run the full build pipeline

```bash
# Using the build script
./scripts/build.sh all

# Or directly with Gradle
./gradlew clean build
```

### 3. Run tests with coverage

```bash
./gradlew test jacocoTestReport
# Report: build/reports/jacoco/test/html/index.html
```

### 4. Run the application

```bash
./gradlew run
```

---

## Gradle Build Automation

The `build.gradle` file provides the following custom tasks:

| Task | Group | Description |
|------|-------|-------------|
| `cleanBuildDeploy` | DevOps | Full pipeline: clean → build → test → package |
| `fatJar` | DevOps | Packages app with all dependencies into a single JAR |
| `printDependencies` | DevOps | Lists all resolved runtime dependencies |
| `generateBuildInfo` | DevOps | Writes `build/build-info.properties` with metadata |

```bash
# List all available tasks
./gradlew tasks

# Run specific DevOps tasks
./gradlew fatJar
./gradlew printDependencies
./gradlew generateBuildInfo
```

---

## Dependency Management

Dependencies are declared in `build.gradle`:

```groovy
dependencies {
    // Production
    implementation 'org.slf4j:slf4j-api:2.0.12'
    implementation 'ch.qos.logback:logback-classic:1.5.3'
    implementation 'com.google.guava:guava:33.1.0-jre'
    implementation 'org.apache.commons:commons-lang3:3.14.0'
    implementation 'com.fasterxml.jackson.core:jackson-databind:2.17.0'

    // Test
    testImplementation 'org.junit.jupiter:junit-jupiter-api:5.10.2'
    testImplementation 'org.mockito:mockito-core:5.11.0'
}
```

Gradle caches dependencies at `~/.gradle/caches/`. The CI/CD pipeline caches this directory to speed up builds.

---

## CI/CD Pipeline (GitHub Actions)

The pipeline in `.github/workflows/ci-cd.yml` runs on every push and PR:

```
Push to main/develop
        │
        ▼
┌───────────────┐     ┌───────────────┐
│ Build & Test  │────▶│ Code Quality  │
│  (JUnit 5 +   │     │  (JaCoCo 70%) │
│   JaCoCo)     │     └───────┬───────┘
└───────────────┘             │
                              ▼
                    ┌───────────────────┐
                    │ Package (fat JAR) │
                    └────────┬──────────┘
                             │  (main branch only)
                             ▼
                    ┌───────────────────┐
                    │  Docker Build     │
                    │  & Push           │
                    └────────┬──────────┘
                             │
                             ▼
                    ┌───────────────────┐
                    │  Deploy Staging   │
                    └───────────────────┘
```

### Required GitHub Secrets

| Secret | Description |
|--------|-------------|
| `DOCKER_USERNAME` | Docker Hub username |
| `DOCKER_PASSWORD` | Docker Hub access token |
| `STAGING_HOST` | Staging server IP/hostname |
| `STAGING_USER` | SSH username |
| `STAGING_SSH_KEY` | SSH private key |

---

## Docker

### Build and run locally

```bash
# Using the deploy script
./scripts/deploy.sh all

# Or manually
./gradlew fatJar
docker build -f docker/Dockerfile -t java-devops-project:local .
docker run --name java-devops-app -p 8080:8080 java-devops-project:local
```

### Docker Compose (with log volume)

```bash
cd docker
docker-compose up -d
docker-compose logs -f
```

---

## Code Coverage

JaCoCo is configured to enforce a **minimum 70% coverage threshold**.

```bash
./gradlew jacocoTestCoverageVerification   # fails build if < 70%
./gradlew jacocoTestReport                 # generates HTML report
```

Open `build/reports/jacoco/test/html/index.html` in a browser to view the full report.

---

## Core DevOps Principles Applied

| Principle | Implementation |
|-----------|---------------|
| **Automated Builds** | Gradle wrapper ensures reproducible builds on any machine |
| **Dependency Management** | Explicit versioning, Gradle cache, dependency reports |
| **Continuous Integration** | GitHub Actions pipeline on every push |
| **Continuous Delivery** | Automatic Docker build + staging deploy on `main` |
| **Test Automation** | JUnit 5 unit tests, JaCoCo coverage enforcement |
| **Infrastructure as Code** | Dockerfile, docker-compose.yml, GitHub Actions YAML |
| **Artifact Management** | Fat JAR + Docker images with versioned tags |
| **Observability** | SLF4J + Logback structured logging, rolling file appender |
