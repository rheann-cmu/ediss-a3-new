FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/mobilebff-0.0.1-SNAPSHOT.jar app.jar
COPY src/main/resources/application.properties application.properties
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

