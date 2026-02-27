# ─── Stage 1: Build ───────────────────────────────────────────────────────────
FROM gradle:8.5-jdk21-alpine AS builder

WORKDIR /app

# Copiar solo el gradle primero (aprovecha el cache de capas)
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
RUN gradle dependencies --no-daemon || true

# Copiar el resto del código y compilar
COPY src ./src
RUN gradle buildFatJar --no-daemon

# ─── Stage 2: Runtime (imagen mucho más pequeña) ──────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/build/libs/*-all.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]