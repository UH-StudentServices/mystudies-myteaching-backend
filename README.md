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
127.0.0.1       my-studies-redis
127.0.0.1       my-studies-psql
```

3. Docker and Docker Compose must be installed and running. Before running application or tests, start dockerized dependencies (psql and redis)

For local development:

```
cd docker/local-dev
docker-compose up
```

For local test runs:

```
cd docker/local-test
docker-compose up
```

### Running tests

`./gradlew test`

### Running tests in CI

Tests can be run in fully dockerized environment where no ports are exposed to host machine. This is intended to be used on CI machine, 
where multiple tests can run in parallel. 

```
cd docker/ci
docker-compose run --entrypoint "<gradle command>" my-studies-builder
docker-compose stop
docker-compose rm -f
```

### Building runnable jars

First start docker containers for test environment

Then run: `./gradlew build`

Built jar files can be found in `build/libs` directory

### Running jars locally

First start docker containers for local dev PostgreSQL and redis

Then run: `java -jar build/libs/app-{VERSION_NUMBER}-SNAPSHOT.jar --spring.profiles.active=local-dev`

Replace VERSION_NUMBER with an actual version number present in the jar file name in `build/libs` directory.
