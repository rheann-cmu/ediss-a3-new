FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/webappbff-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties application.properties
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]

