# Stage 1: Build
FROM maven:3.9.9-eclipse-temurin-21 AS build
ARG MODULE=ik-mat
WORKDIR /app
COPY pom.xml .
COPY ik-common/pom.xml ik-common/
COPY ik-mat/pom.xml ik-mat/
COPY ik-alkohol/pom.xml ik-alkohol/
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn package -pl ${MODULE} -am -DskipTests -B

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
ARG MODULE=ik-mat
WORKDIR /app
COPY --from=build /app/${MODULE}/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
