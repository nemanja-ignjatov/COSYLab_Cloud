FROM openjdk:11.0.8-jre
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} cloud-discovery.jar
ENTRYPOINT ["java","-jar","/cloud-discovery.jar"]
