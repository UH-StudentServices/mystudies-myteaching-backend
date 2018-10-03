#!/bin/bash

job=my-backend-publish
build_cache_prefix=my-backend-master-build

/usr/local/bin/docker-compose -p $job run --no-deps -v $build_cache_prefix-gradle:/home/jenkins/.gradle -v $build_cache_prefix-m2:/home/jenkins/.m2 --rm --user jenkins --entrypoint "gradle publish -x test -Penvironment=prod -Popintoni_artifactory_base_url=${ARTIFACTORY_BASE_URL} -Popintoni_artifactory_username=${ARTIFACTORY_USERNAME} -Popintoni_artifactory_password=${ARTIFACTORY_PASSWORD}" my-studies-builder


