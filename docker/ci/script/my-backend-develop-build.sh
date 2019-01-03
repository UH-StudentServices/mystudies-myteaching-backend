#!/bin/bash

# Sonar login $SONARQUBE_TOKEN is stored in Jenkins credentials

./run.sh my-backend-develop-build "gradle --info storeDeployInformationToFile publish --stacktrace --refresh-dependencies -Psonar=true -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$SONARQUBE_TOKEN"
