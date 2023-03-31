# Build
FROM maven:3.8.5-amazoncorretto-17 as build

COPY pom.xml /usr/app/
COPY src /usr/app/src
COPY .mvn /usr/app/.mvn

WORKDIR /usr/app
RUN mvn --update-snapshots --no-transfer-progress --errors clean verify

# Run
FROM eclipse-temurin:17.0.3_7-jre-focal

ARG JAR_FILE=target/*.jar
COPY --from=build /usr/app/$JAR_FILE /usr/local/lib/recipy-backend.jar

ENTRYPOINT ["java","-jar","/usr/local/lib/recipy-backend.jar"]
