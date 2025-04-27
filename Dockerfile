# ---- Build the application with JDK 21 ----
FROM gradle:8.4.0-jdk21 AS builder
COPY . /home/app
WORKDIR /home/app
RUN gradle bootJar --no-daemon

# ---- Run the application with lightweight JDK 21 image ----
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY --from=builder /home/app/build/libs/*.jar app.jar

# порт, на котором работает сервис
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]