FROM openjdk:11.0.8-jre
ARG JAR_FILE=target/*.jar
ARG CONFIG_FILES=./cloud_config_repo

COPY ${CONFIG_FILES} /home
COPY ${JAR_FILE} cosy_cloud_config.jar

ENTRYPOINT ["java","-jar","/cosy_cloud_config.jar"]
