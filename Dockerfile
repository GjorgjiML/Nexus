# Build stage
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S nexus && adduser -S nexus -G nexus \
    && mkdir -p /app/uploads \
    && chown -R nexus:nexus /app

COPY --from=build /app/target/nexus-1.0.0.jar app.jar

USER nexus

ENV JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=dev \
    UPLOAD_DIR=/app/uploads

EXPOSE 8080

HEALTHCHECK --interval=15s --timeout=5s --start-period=40s --retries=5 \
  CMD wget -qO- http://127.0.0.1:8080/login >/dev/null 2>&1 || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
