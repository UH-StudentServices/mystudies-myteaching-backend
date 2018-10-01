#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose run -v ~/.gradle-docker/my-backend-master-build:/home/jenkins/.gradle --rm --user jenkins --entrypoint "gradle release --refresh-dependencies -Penvironment=prod -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} -Pgradle.release.useAutomaticVersion=true" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f

