#!/bin/bash

cd ..;

cd ./Cloud_Config_Server;
mvn clean install -DskipTests;
docker build -t ignjatov90/cosylab-cloud-config-server .;

cd ..;

cd ./Cloud_Discovery_Server;
mvn clean install -DskipTests;
docker build -t ignjatov90/cosylab-cloud-discovery-server .;

cd ..;

cd ./Cloud_CSWA;
gulp build;
docker build -t ignjatov90/cosylab-cloud-cswa .;

cd ..;

cd ./Cloud_ACAM;
mvn clean install -DskipTests;
docker build -t ignjatov90/cosylab-cloud-acam .;

cd ..;

cd ./Cloud_TNTA;
mvn clean install -DskipTests;
docker build -t ignjatov90/cosylab-cloud-tnta .;


read -p "Press any key to continue... " -n1 -s;
