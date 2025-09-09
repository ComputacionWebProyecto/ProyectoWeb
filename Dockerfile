FROM openjdk:24

COPY . /app

WORKDIR /app

RUN ./mvnw clean install -DskipTests

CMD ["java", "-jar", "target/entrega-0.0.1-SNAPSHOT.jar"]

