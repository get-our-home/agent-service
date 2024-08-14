FROM openjdk:17-jdk

WORKDIR /app

COPY build/libs/*.jar agent-service.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "agent-service.jar"]