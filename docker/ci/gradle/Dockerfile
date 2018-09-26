FROM java:openjdk-8-alpine

RUN apk update \
  && apk add bash ca-certificates wget \
  && update-ca-certificates \
  && wget https://services.gradle.org/distributions/gradle-2.9-bin.zip \
  && mkdir -p /opt/gradle \
  && unzip -d /opt/gradle gradle-2.9-bin.zip

ENV PATH="/opt/gradle/gradle-2.9/bin:${PATH}"

