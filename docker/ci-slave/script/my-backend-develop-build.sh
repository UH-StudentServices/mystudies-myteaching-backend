#!/bin/bash

./run.sh my-backend-develop-build "gradle storeDeployInformationToFile publish --stacktrace --refresh-dependencies -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}"
