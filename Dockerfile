# Stage 1: Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY graduation-backend/pom.xml .
COPY graduation-backend/src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
