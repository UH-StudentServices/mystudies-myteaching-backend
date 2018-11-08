#!/bin/bash

USER_NAME="$(id -un)"
USER_ID="$(id -u)"
GROUP_NAME="$(id -gn)"
GROUP_ID="$(id -g)"

PATH=$PATH:/usr/local/bin

stop_and_remove_containers() {
  docker-compose -p $1 rm -fsv
}

stop_and_remove_containers $1
docker-compose build --build-arg USER_NAME="$USER_NAME" --build-arg USER_ID=$USER_ID --build-arg GROUP_NAME="$GROUP_NAME" --build-arg GROUP_ID=$GROUP_ID
docker-compose -p $1 run \
-v ~/.ssh/:/home/$USER_NAME/.ssh:ro \
-v ~/.gitconfig:/home/$USER_NAME/.gitconfig \
-v $1-gradle:/home/$USER_NAME/.gradle \
-v $1-m2:/home/$USER_NAME/.m2 \
--rm --user $USER_NAME --entrypoint "$2" my-studies-builder
exit_code=$?
stop_and_remove_containers $1
exit $exit_code
