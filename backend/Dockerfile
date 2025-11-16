# Multi-stage Dockerfile for building and running the ecomarket backend
# Stage 1: Builder (build + run tests)
FROM maven:3.9.6-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy everything and build. Using full copy keeps it simple; for faster caching
# you can copy pom.xml and download dependencies separately.
COPY . .

# Ensure Maven runs non-interactively and runs tests (no -DskipTests)
RUN mvn -B clean package

# Stage 2: Runtime
FROM eclipse-temurin:23-jre

WORKDIR /app

# Copy the fat JAR from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose default port used in dev profile
EXPOSE 8090

# Run the application. Use ${PORT} if provided by the platform (Render) otherwise
# fall back to 8090 (matches `application-dev.properties`). Any `SPRING_*` env
# variables will be read by Spring Boot automatically.
ENV JAVA_OPTS="-Xms256m -Xmx512m"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8090} -jar /app/app.jar"]
