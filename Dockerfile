FROM maven:3.9.5-openjdk-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package

FROM openjdk:21-jdk-slim
WORKDIR /app
COPY target/*.jar app.jar
ENV TZ=America/Sao_Paulo
RUN chmod +x app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
