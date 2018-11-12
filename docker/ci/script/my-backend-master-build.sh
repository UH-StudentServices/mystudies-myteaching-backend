#!/bin/bash

./run.sh my-backend-master-build "gradle release --refresh-dependencies -Partifactory=${ARTIFACTORY_BASE_URL}/remote-repos/ -Penvironment=prod -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} -Pgradle.release.useAutomaticVersion=true"

