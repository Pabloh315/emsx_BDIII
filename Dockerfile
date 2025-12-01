# -----------------------------
# STAGE 1: Build the JAR file
# -----------------------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy Maven descriptors
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY mvnw.cmd .

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests

# -----------------------------
# STAGE 2: Run the application
# -----------------------------
FROM eclipse-temurin:17-jdk

WORKDIR /app

# Copy only the JAR from phase 1
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

# Default port used by Render
ENV PORT=8080

ENTRYPOINT ["java", "-jar", "app.jar"]
