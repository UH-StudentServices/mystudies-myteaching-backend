#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose run --rm --user jenkins -v ~/.gradle-docker/my-backend-develop-build:/home/jenkins/.gradle --entrypoint "gradle storeDeployInformationToFile publish --refresh-dependencies -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f
