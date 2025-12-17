# -----------------------------
# STAGE 1: Build the JAR file
# -----------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copiar archivos de Maven y configuración
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .
COPY src ./src

# 🔧 Dar permisos de ejecución a mvnw (SOLUCIÓN AL ERROR 126)
RUN chmod +x mvnw

# Construir la aplicación (sin tests)
RUN ./mvnw clean package -DskipTests

# -----------------------------
# STAGE 2: Run the application
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copiar solo el JAR generado en la etapa anterior
COPY --from=build /app/target/*.jar app.jar

# Puerto dinámico asignado por Render
# Render inyecta la variable PORT automáticamente
EXPOSE $PORT

# Comando de inicio - Pasa PORT dinámicamente a Spring Boot
ENTRYPOINT ["sh", "-c", "java -jar -Dserver.port=$PORT app.jar"]
