#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose run -v my-backend-build-and-deploy-snapshot-gradle:/home/jenkins/.gradle -v my-backend-build-and-deploy-snapshot-m2:/home/jenkins/.m2 --rm --user jenkins --entrypoint "storeDeployInformationToFile publish -Penvironment=$ENVIRONMENT -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f -v