# Stage 1: Build all modules
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy POMs first for dependency caching
COPY pom.xml .
COPY ik-common/pom.xml ik-common/
COPY ik-food-service/pom.xml ik-food-service/
COPY ik-alcohol-service/pom.xml ik-alcohol-service/
RUN mvn dependency:go-offline -B

# Copy sources and build
COPY . .
RUN mvn package -B -DskipTests

# Stage 2: Run the selected service
FROM eclipse-temurin:21-jre-alpine
ARG SERVICE_MODULE
WORKDIR /app
COPY --from=build /app/${SERVICE_MODULE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
