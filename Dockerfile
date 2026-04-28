# Build frontend
FROM node:20-alpine AS frontend-build
WORKDIR /app/frontend
COPY student-portfolio-frontend/package*.json ./
RUN npm ci
COPY student-portfolio-frontend .
RUN npm run build

# Build backend
FROM maven:3.9.8-eclipse-temurin-17 AS backend-build
WORKDIR /app/backend
COPY student-portfolio/pom.xml ./
RUN mvn -B dependency:go-offline dependency:resolve
COPY student-portfolio/src ./src
COPY --from=frontend-build /app/frontend/dist ./src/main/resources/static
RUN mvn -B package -DskipTests

# Runtime image
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=backend-build /app/backend/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
