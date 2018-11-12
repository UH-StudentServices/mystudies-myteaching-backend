#!/bin/bash

./run.sh my-backend-check-pull-request "gradle test --info -Penvironment=dev -Partifactory=${ARTIFACTORY_BASE_URL}/remote-repos/ -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} --stacktrace"

