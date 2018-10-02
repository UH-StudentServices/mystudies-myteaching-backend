#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose run --rm -v my-backend-develop-build-gradle:/home/jenkins/.gradle -v my-backend-develop-build-m2:/home/jenkins/.m2 --user jenkins --entrypoint "gradle storeDeployInformationToFile publish --stacktrace --refresh-dependencies -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f -v
