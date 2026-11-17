# Dockerfile for SI-Paket Backend
# Multi-stage build untuk ukuran image yang lebih kecil

# Stage 1: Build
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app

# Copy source code
COPY src/ ./src/
COPY lib/ ./lib/
COPY MainApp.java ./

# Compile
RUN mkdir -p bin && \
    javac -cp "lib/*:src" -d bin src/**/*.java MainApp.java

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy compiled classes & dependencies
COPY --from=builder /app/bin ./bin
COPY --from=builder /app/lib ./lib

# Expose port
EXPOSE 8080

# Environment variables (akan di-set di Render Dashboard)
# DB_URL, DB_USER, DB_PASSWORD, JWT_SECRET, JWT_EXPIRATION_MS

# Run
ENTRYPOINT ["java", "-cp", "lib/*:bin", "MainApp"]