# Build
# FROM maven:17.0.2-jdk-slim as build
FROM maven:3.8.5-amazoncorretto-17 as build

COPY pom.xml /usr/app/
COPY src /usr/app/src
COPY .mvn /usr/app/.mvn

WORKDIR /usr/app
RUN mvn clean package

# Run
# FROM arm64v8/openjdk:17.0.2-jdk-slim
FROM eclipse-temurin:17.0.3_7-jre-focal

ARG JAR_FILE=target/*.jar
COPY --from=build /usr/app/$JAR_FILE /usr/local/lib/recipy-backend.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/recipy-backend.jar"]
