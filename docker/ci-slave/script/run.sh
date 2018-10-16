#!/bin/bash

/usr/local/bin/docker-compose -p $1 run -v $1-gradle:/home/ci/.gradle -v $1-m2:/home/ci/.m2 --rm --user ci --entrypoint "$2" my-studies-builder
exit_code=$?
/usr/local/bin/docker-compose -p $1 stop
/usr/local/bin/docker-compose -p $1 rm -f -v
exit $exit_code
