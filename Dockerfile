# ================= STAGE 1: Build =================
# Maven va JDK 21 bo'lgan asosiy image'dan foydalanamiz loyihani qurish uchun
FROM maven:3.9-eclipse-temurin-21 AS builder

# Ilova uchun ishchi katalog yaratamiz
WORKDIR /app

# 1. Faqat pom.xml ni ko'chiramiz va bog'liqliklarni (dependencies) yuklab olamiz.
# Bu qadam faqat pom.xml o'zgarganda qayta ishlaydi, bu esa build'ni tezlashtiradi.
COPY pom.xml .
RUN mvn dependency:go-offline

# 2. Qolgan barcha kodni (src papkasini va h.k.) ko'chiramiz
COPY src ./src

# 3. Loyihani testlarsiz "package" qilamiz. Bu `target` papkasini va JAR faylni yaratadi.
RUN mvn clean package -DskipTests


# ================= STAGE 2: Run =================
# Faqat Java Runtime (JRE) bo'lgan kichik va xavfsiz image'dan foydalanamiz
FROM openjdk:21-jre-slim

# Ilova uchun ishchi katalog
WORKDIR /app

# Birinchi bosqich (builder) dan faqat qurilgan JAR faylni ko'chiramiz
# `target` papkasidagi `...-SNAPSHOT.jar` faylini olib, `app.jar` deb nomlaymiz
COPY --from=builder /app/target/*-SNAPSHOT.jar app.jar

# Ilova 8080-portda ishlashini belgilaymiz
EXPOSE 8080

# Konteyner ishga tushganda bajariladigan asosiy buyruq
ENTRYPOINT ["java", "-jar", "app.jar"]
