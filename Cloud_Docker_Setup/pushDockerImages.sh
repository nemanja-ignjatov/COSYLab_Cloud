#!/bin/bash

docker push ignjatov90/cosylab-cloud-discovery-server;
docker push ignjatov90/cosylab-cloud-config-server;

docker push ignjatov90/cosylab-cloud-acam;
docker push ignjatov90/cosylab-cloud-cswa;
docker push ignjatov90/cosylab-cloud-tnta;

read -p "Press any key to continue... " -n1 -s;


