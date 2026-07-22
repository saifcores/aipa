# syntax=docker/dockerfile:1.7

FROM maven:3.9.11-eclipse-temurin-26 AS build
WORKDIR /workspace
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

FROM eclipse-temurin:26-jre
WORKDIR /app
RUN useradd --system --uid 10001 aipa
COPY --from=build /workspace/target/payment-ai-assistant-*.jar /app/app.jar
USER aipa
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
