#!/bin/sh

# run the tests with the gradle wrapper
./gradlew checkstyle

# store the last exit code in a variable
RESULT=$?

# return the './gradlew test' exit code
exit $RESULT
