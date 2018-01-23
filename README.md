# MyStudies/MyTeaching Backend

## Requirements

The following programs must be installed
- JDK 8

The following directories must exist on host machine
- ~/.m2
- ~/.gradle

## Running locally

### Prerequisites

1. Add the following empty properties to ~/.gradle/gradle.properties
(they need to be defined but are only required in server environments):

```
opintoni_artifactory_base_url=
opintoni_artifactory_username=
opintoni_artifactory_password=
```

2. Add following localhost alias configurations (in /etc/hosts on Linux/macOS)

```
127.0.0.1       local.student.helsinki.fi
127.0.0.1       local.teacher.helsinki.fi
```

3. Docker must be installed and running when running tests and building jars from the command line.

### Running tests

First start Redis in a Docker container: `./app/src/test/script/start-redis.sh`.

Then run tests: `./app/gradlew test`

### Building runnable jars

First start Redis in a Docker container: `./app/src/test/script/start-redis.sh`.

Then run: `./app/gradlew build`

Built jar files can be found in `app/build/libs` directory

### Running jars locally

`java -jar app/build/libs/app-{VERSION_NUMBER}-SNAPSHOT.jar --spring.profiles.active=local-dev`

Replace VERSION_NUMBER with an actual version number present in the jar file name in `app/build/libs` directory.
