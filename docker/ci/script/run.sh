#!/bin/bash

USER_NAME="$(id -un)"
USER_ID="$(id -un)"
GROUP_NAME="$(id -gn)"
GROUP_ID="$(id -g)"

stop_and_remove_containers() {
  /usr/local/bin/docker-compose -p $1 stop
  /usr/local/bin/docker-compose -p $1 rm -f -v
}

stop_and_remove_containers $1
/usr/local/bin/docker-compose build --build-arg USER_NAME=$USER_NAME --build-arg USER_ID=$USER_ID --build-arg GROUP_NAME=$GROUP_NAME --build-arg GROUP_ID=$GROUP_ID
/usr/local/bin/docker-compose -p $1 run un -v ~/.ssh/:/home/$USER_NAME/.ssh:ro -v ~/.gitconfig:/home/$USER_NAME/.gitconfig -v $1-gradle:/home/$USER_NAME/.gradle -v $1-m2:/home/$USER_NAME/.m2 --rm --user jenkins --entrypoint "$2" my-studies-builder
exit_code=$?
stop_and_remove_containers $1
exit $exit_code
