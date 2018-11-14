#!/bin/bash

./run.sh my-backend-publish "gradle publish -x test -Partifactory=${ARTIFACTORY_BASE_URL}/remote-repos/ -Penvironment=prod -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}"
