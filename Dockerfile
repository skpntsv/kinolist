FROM openjdk:17.0.2-jdk-slim-buster
ARG JAR_FILE=kinolist/target/kinolist-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
