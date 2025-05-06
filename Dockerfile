FROM openjdk:17-jdk-alpine

ARG JAR_FILE=target/*.jar

RUN mkdir -p /app

WORKDIR /app

COPY ${JAR_FILE} /app/salaries-0.0.1.jar

EXPOSE 8080

CMD ["java", "-jar", "/app/salaries-0.0.1.jar"]