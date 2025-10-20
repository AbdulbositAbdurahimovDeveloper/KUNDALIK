# 1-qadam: Asosiy image (JDK 21)
FROM openjdk:21

# 2-qadam: Ilova uchun ishchi katalog
WORKDIR /app

# 3-qadam: app.jar ni konteynerga ko‘chirish
COPY app.jar kundalik_app.jar

# 4-qadam: Ilovani ishga tushirish
ENTRYPOINT ["java", "-jar", "kundalik_app.jar"]
