#!/bin/bash

./run.sh my-backend-check-pull-request "gradle test --info -Penvironment=dev -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD} --stacktrace && gradle sonarqube --stacktrace -Dsonar.analysis.mode=preview -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$SONARCUBE_TOKEN -Dsonar.github.repository=UH-StudentServices/mystudies-myteaching-backend -Dsonar.github.pullRequest=${ghprbPullId} -Dsonar.github.oauth=$SONAR_GITHUB_TOKEN"
