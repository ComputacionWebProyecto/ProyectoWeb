# Etapa 1: build con Maven
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean install -DskipTests

# Etapa 2: imagen final para ejecutar
FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/entrega-0.0.1-SNAPSHOT.jar app.jar
CMD ["java", "-jar", "app.jar"]
