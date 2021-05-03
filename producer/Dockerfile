FROM openjdk:8-jdk-alpine
COPY ./target/producer-0.0.1-SNAPSHOT.jar producer-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","producer-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080