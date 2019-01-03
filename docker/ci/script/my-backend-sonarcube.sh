#!/bin/bash

./run.sh my-backend-sonarcube "gradle sonarqube --info -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} --stacktrace -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$SONARCUBE_TOKEN"
