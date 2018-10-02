#!/bin/bash

./run.sh my-backend-build-and-deploy-snapshot "storeDeployInformationToFile publish -Penvironment=${ENVIRONMENT} -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}"