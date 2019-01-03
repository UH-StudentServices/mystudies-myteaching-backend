#!/bin/bash

# Sonar login $SONARQUBE_TOKEN is stored in Jenkins credentials

./run.sh my-backend-sonarcube "gradle sonarqube --info -Penvironment=dev --stacktrace -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$SONARQUBE_TOKEN"
