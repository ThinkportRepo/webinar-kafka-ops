FROM openjdk:8-jdk-alpine
COPY ./target/consumer-0.0.1-SNAPSHOT.jar consumer-0.0.1-SNAPSHOT.jar
RUN ["cp", "/usr/lib/jvm/java-1.8-openjdk/jre/lib/security/cacerts", "/tmp/kafka.client.truststore.jks"]
ENTRYPOINT ["java", "-jar","consumer-0.0.1-SNAPSHOT.jar"]
EXPOSE 8080