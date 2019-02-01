#!/bin/bash

# Sonar login $sonar_mystudies_myteaching_backend_token & github token $sonar_github_plugin_token are stored in Jenkins credentials
./run.sh my-backend-check-pull-request "gradle sonarqube --info -Penvironment=dev --stacktrace -Dsonar.analysis.mode=preview -Dsonar.host.url=https://opa-ci.it.helsinki.fi/sonar/ -Dsonar.login=$sonar_mystudies_myteaching_backend_token -Dsonar.github.repository=UH-StudentServices/mystudies-myteaching-backend -Dsonar.github.pullRequest=${ghprbPullId} -Dsonar.github.oauth=$sonar_github_plugin_token"
