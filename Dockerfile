# 1) Build stage
FROM openjdk:17-jdk-slim AS builder
WORKDIR /workspace

# Gradle wrapper and settings
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Copy source
COPY src src

# Build the application (skip tests if needed)
RUN chmod +x gradlew && \
    ./gradlew bootJar --no-daemon

# 2) Run stage
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copy the jar from builder
COPY --from=builder /workspace/build/libs/*.jar app.jar

# Expose application port
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
