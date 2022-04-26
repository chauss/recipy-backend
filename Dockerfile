# Safe for later, raspberry pi 2 is too old
FROM alpine:latest

COPY src /usr/app/src
COPY pom.xml /usr/app/
COPY mvnw /usr/app/
COPY .mvn /usr/app/.mvn

WORKDIR /usr/app
RUN ./mvnw clean package

ARG JAR_FILE=target/*.jar

ENTRYPOINT ["/opt/jdk/jvm/jdk-17.0.3/bin/java","-jar","$JAR_FILE"]
