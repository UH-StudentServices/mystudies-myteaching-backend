# MyStudies/MyTeaching Backend


## Requirements

The following programs must be installed
- JDK 11

The following directories must exist on host machine
- ~/.m2
- ~/.gradle

## Running locally

### Prerequisites

1. Add following localhost alias configurations (in /etc/hosts on Linux/macOS)

```
127.0.0.1       local.student.helsinki.fi
127.0.0.1       local.teacher.helsinki.fi
127.0.0.1       my-studies-redis
127.0.0.1       my-studies-psql
```

2. Docker and Docker Compose must be installed and running. Before running application or tests, start dockerized dependencies (psql and redis)

For local development:

```
docker-compose  -f docker/local-dev/docker-compose.yml up
```

For local test runs:

```
docker-compose  -f docker/local-test/docker-compose.yml up
```

### Obar integration

Configuration key obar.baseUrl ([local config](https://github.com/UH-StudentServices/mystudies-myteaching-backend/blob/develop/src/main/resources/config/application.yml)) controls use of obar.
See [frontend readme](https://github.com/UH-StudentServices/mystudies-myteaching-frontend/blob/develop/README.md) for details.

To use Obar in local development, uncomment the url value from a obar.baseUrl property from application.yml. This property must be left empty when
local Opintoni header is used (for instance when running e2e tests). When using local url for obar.baseUrl, Obar application must be running locally.

### Running with gradle

`./gradlew bootRun`

### Running with debugging enabled

`./gradlew bootRun ----debug-jvm`

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

