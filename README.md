Mystudies Myteaching
================

Requirements
---------------

The following programs must be installed
- JDK 8

The following directories must exist in host machine
- ~/.m2
- ~/.gradle

Running locally
---------------
### 1. Prerequisites
Add the following empty properties to ~/.gradle/gradle.properties
Properties need to be defined but are only required at automated build server Jenkis

```
opintoni_artifactory_base_url=
opintoni_artifactory_username=
opintoni_artifactory_password=
```

Add following localhost alias configurations (/etc/hosts)

```sh
127.0.0.1       local.student.helsinki.fi
127.0.0.1       local.teacher.helsinki.fi
```

### 2. Execute tests

Docker must be installed and running

```sh
cd app
./src/test/script/start-redis.sh
./gradlew test
```

### 3. Build runnable jar
```sh
./gradlew build
```
Built jar can be found in the build/libs directory

### 4. Run backend jar locally
```sh
java -jar build/libs/app-{VERSION_NUMBER}.jar --spring.profiles.active=local-dev
```
See the actual VERSION_NUMBER from the jar file name in build/lib directory