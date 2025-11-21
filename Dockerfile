# Multi-stage Dockerfile for EcoMarket - Backend + Frontend
# Stage 1: Build Frontend (Angular)
FROM node:22-alpine AS frontend-builder

WORKDIR /app/frontend

# Copy frontend package files
COPY frontend-angular/package*.json ./

# Set environment variable to skip Puppeteer download
ENV PUPPETEER_SKIP_DOWNLOAD=true

# Install dependencies with legacy peer deps
RUN npm install --legacy-peer-deps

# Copy frontend source code
COPY frontend-angular/ ./

# Build the Angular application for production
RUN npm run build:spa

# Stage 2: Build Backend (Spring Boot)
FROM maven:3.9.6-eclipse-temurin-22 AS backend-builder

WORKDIR /app

# Copy backend source code
COPY backend/ ./

# Build Spring Boot application (runs tests)
RUN mvn -B clean package

# Stage 3: Runtime - Serve both Frontend and Backend
FROM eclipse-temurin:22-jre

WORKDIR /app

# Install nginx to serve static files
RUN apt-get update && apt-get install -y nginx && rm -rf /var/lib/apt/lists/*

# Copy the Spring Boot JAR from backend builder
COPY --from=backend-builder /app/target/*.jar app.jar

# Copy the built Angular app from frontend builder
COPY --from=frontend-builder /app/frontend/dist/frontend-angular/browser/browser /var/www/html

# Create nginx configuration
RUN echo 'server { \
    listen 80; \
    server_name localhost; \
    root /var/www/html; \
    index index.html; \
    \
    # Serve Angular app \
    location / { \
        try_files $uri $uri/ /index.html; \
    } \
    \
    # Proxy API requests to Spring Boot \
    location /ecomarket/api/ { \
        proxy_pass http://localhost:8080; \
        proxy_set_header Host $host; \
        proxy_set_header X-Real-IP $remote_addr; \
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; \
        proxy_set_header X-Forwarded-Proto $scheme; \
    } \
}' > /etc/nginx/sites-available/default

# Create startup script
RUN echo '#!/bin/bash \
java -Dserver.port=8080 -jar /app/app.jar & \
nginx -g "daemon off;"' > /app/start.sh && chmod +x /app/start.sh

# Expose port 80 for nginx (frontend) and 8080 for Spring Boot (backend)
EXPOSE 80

# Start both services
CMD ["/app/start.sh"]
