#!/bin/bash

cd docker/ci
/usr/local/bin/docker-compose run -v ~/.gradle-docker/my-backend-check-pull-request:/home/jenkins/.gradle --rm --user jenkins --entrypoint "gradle test -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder
/usr/local/bin/docker-compose stop
/usr/local/bin/docker-compose rm -f
