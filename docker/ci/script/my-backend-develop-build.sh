#!/bin/bash

./run.sh my-backend-develop-build "gradle --info storeDeployInformationToFile publish --stacktrace -Partifactory=http://opi-1.student.helsinki.fi:8081/artifactory/remote-repos/ -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}"
