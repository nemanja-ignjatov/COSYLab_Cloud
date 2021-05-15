FROM openjdk:11.0.8-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} tnta.jar
ENTRYPOINT ["java","-jar","/tnta.jar"]
