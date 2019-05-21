#!/bin/bash

./run.sh my-backend-master-build "gradle release --refresh-dependencies -Penvironment=prod -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} -Prelease.useAutomaticVersion=true"

