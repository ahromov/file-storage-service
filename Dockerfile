FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

COPY target/*.jar /app/file-storage-service.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=docker", "/app/file-storage-service.jar"]
