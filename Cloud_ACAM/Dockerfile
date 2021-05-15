FROM openjdk:11.0.8-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} acam.jar
ENTRYPOINT ["java","-jar","/acam.jar"]
