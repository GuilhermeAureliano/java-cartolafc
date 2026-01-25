FROM maven:3.9.5-amazoncorretto-21-al2023 AS build
WORKDIR /app

COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw -B -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:21-jre-alpine

LABEL org.opencontainers.image.title="CartolaFC API" \
      org.opencontainers.image.description="Interface Java para consumir a API REST do Cartola FC" \
      org.opencontainers.image.version="0.0.1-SNAPSHOT" \
      org.opencontainers.image.authors="Guilherme Aureliano" \
      org.opencontainers.image.source="https://github.com/guilhermeaureliano/java-cartolafc" \
      org.opencontainers.image.licenses="MIT"

WORKDIR /app

COPY --from=build /app/target/cartolafc-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

USER 10001

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
