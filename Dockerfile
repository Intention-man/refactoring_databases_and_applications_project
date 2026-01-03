# Multi-stage build для оптимизации размера образа
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

# Копируем pom.xml и загружаем зависимости (кэширование слоев)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Копируем исходный код и собираем приложение
COPY src ./src
RUN mvn clean package -DskipTests

# Финальный образ
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Создаем непривилегированного пользователя
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Копируем JAR из stage сборки
COPY --from=build /app/target/*.jar app.jar

# Открываем порт
EXPOSE 18123

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]



