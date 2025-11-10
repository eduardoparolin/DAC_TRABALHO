# Build stage
FROM maven:3.9.2-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/orchestrator-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8085
CMD ["java", "-jar", "app.jar"]
