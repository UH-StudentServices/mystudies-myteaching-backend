#!/bin/bash

# Sonar login $SONARQUBE_TOKEN & github token $SONAR_GITHUB_TOKEN are stored in Jenkins credentials

./run.sh my-backend-check-pull-request "gradle sonarqube --info -Penvironment=dev --stacktrace -Dsonar.analysis.mode=preview -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$SONARQUBE_TOKEN -Dsonar.github.repository=UH-StudentServices/mystudies-myteaching-backend -Dsonar.github.pullRequest=${ghprbPullId} -Dsonar.github.oauth=$SONAR_GITHUB_TOKEN"
