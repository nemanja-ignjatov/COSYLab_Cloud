# COSYLab Cloud components

#### General notice

--------------------------------------

This project is developed within the COSYLab IoT framework for Smart Homes, in the scope of the dissertation "Trustworthy Context-Aware
Access Control in IoT Environments based on the Fog Computing Paradigm".
Currently, the design details for the COSYLab are provided through blogs published on the netidee.at website.

Therefore, please visit the following web page for more details on COSYLab:

https://www.netidee.at/trustworthy-context-aware-access-control-iot-environments-based-fog-computing-paradigm

--------------------------------------

### Components
This repository contains Cloud components developed within the COSYLab framework,
offering registry services for trustworthy networking and access control.
Features supported by the components are as follows:
- Cloud_ACAM (Access Control Agent Management) - access control and device types configuration registry services;
- Cloud_TNTA (Trustworthy Networking Trust Anchor) - root PKI node for the COSYLab framework;
- Cloud_CSWA (Cloud Services Web Application) - Browser-based application for using features offered by TNTA and ACAM.

Beside these components, this repository contains supporting Cloud services:
- Cloud_Config_Server - Central management server for TNTA and ACAM runtime configuration;
- Cloud_Discovery_Server - Server used for components discovery, allowing communication between them.

### Dependencies

Developed component are based on following technologies:
- Java 11
- Maven
- Spring Boot
- Spring Cloud Eureka and Spring Cloud Config
- AngularJS
- MongoDB

### Build

Before building Cloud components, COSYLab utilities have to be compiled. To achieve that,
please follow instruction provided in https://github.com/nemanja-ignjatov/COSYLab_Utils.

Once COSYLab utilities are built and installed, each Cloud component can be compiled.
For the Java-based components, this is done by positioning in the directory of the particular component and executing:
> mvn clean install

As for Cloud_CSWA, "gulp" task runner is used for building the executable. 
Thus, CSWA is build using command: 
> gulp build

Once this command is executed, results can be found in "build" folder in CSWA project, 
containing all html, js, and css files for the CSWA deployment.

### Configuration

Once built, each component needs to be configured before its execution.
This is achieved by Spring Cloud Config Server, that by default imports configuration for GitHub repository:
https://github.com/nemanja-ignjatov/COSYLab_Cloud_Config.

### Execution

Once configured, Cloud components can be started as Java applications.
Starting a Cloud component is done by positioning in the component's directory and executing:
> java -jar target/${executable_jar_filename}.jar

For example:
> java-jar target/cloud_acam-1.2.jar

### Docker

Beside standalone application execution, Cloud components can be started in Docker containers.
For that, Docker image needs to be build using command executed in the given component's directory:
>docker build -t ${cloud_components_image_name} .

For example:
>docker build -t ignjatov90/cosylab-cloud-acam .

Additionally, folder Cloud_Docker_Setup contains scripts and Docker Compose configuration file for Cloud Components build and deployment.

Attached Shell scripts are:
- buildDockerImages.sh that creates all required Docker images for Cloud components and services;
- pushDockerImages.sh that uploads locally created Docker images to DockerHub.

Docker Compose configuration is provided in docker-compose.yml file, which can be started using command:
> docker-compose up

or

> docker-compose start

As the defautl Cloud deployment relies on Nginx and reverse-proxy, basic nginx configuration 
is provided in the Cloud_Docker_Setup/cloud_deployment/nginx folder.

