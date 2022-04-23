FROM openjdk:18.0.1-slim
RUN addgroup --system recipy && adduser --system --group recipy
USER recipy:recipy
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]