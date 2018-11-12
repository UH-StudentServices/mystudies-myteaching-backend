#!/bin/bash

./run.sh my-backend-develop-build "gradle --info storeDeployInformationToFile publish --stacktrace --refresh-dependencies -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -PXopintoni_artifactory_password=${ARTIFACTORY_PASSWORD}"
