# ================= STAGE 1: Build =================
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# ================= STAGE 2: Run =================
FROM openjdk:21-slim
WORKDIR /app
COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar
# Server uchun siz kelishgan portlarni e'lon qilamiz
EXPOSE 8080
EXPOSE 4000
ENTRYPOINT ["java", "-jar", "app.jar"]