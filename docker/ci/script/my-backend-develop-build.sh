#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose build --no-cache
/usr/local/bin/docker-compose run --rm --user jenkins --entrypoint "gradle storeDeployInformationToFile publish --stactrace --refresh-dependencies -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f
