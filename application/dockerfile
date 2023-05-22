FROM openjdk:17-jdk-slim

ARG JAR_FILE=target/*.jar
ARG DEBUG_ARGS

COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java"]
CMD ["-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005","-jar","/app.jar"]