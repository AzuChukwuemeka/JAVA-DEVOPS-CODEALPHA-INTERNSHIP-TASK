# ─────────────────────────────────────────────────────────────
# Stage 1: Builder — compile and package the application
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /workspace

# Copy gradle wrapper and build files first (layer caching)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Download dependencies before copying source (improves cache hits)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon 2>/dev/null || true

# Copy source and build
COPY src src
RUN ./gradlew fatJar --no-daemon

# ─────────────────────────────────────────────────────────────
# Stage 2: Runtime — lean production image
# ─────────────────────────────────────────────────────────────
FROM eclipse-temurin:17-jre-alpine

# Create a non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app

# Copy the fat JAR from builder stage
COPY --from=builder /workspace/build/libs/*-all.jar app.jar

# Create logs directory
RUN mkdir -p logs && chown -R appuser:appgroup /app

USER appuser

# JVM tuning for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -Xss512k \
               -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080

LABEL maintainer="devops-team"
LABEL version="1.0.0"
LABEL description="Java DevOps Demo Application"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
