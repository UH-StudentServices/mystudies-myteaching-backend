FROM java:openjdk-8-alpine

RUN apk update \
  && apk add bash ca-certificates wget \
  && update-ca-certificates \
  && wget https://services.gradle.org/distributions/gradle-2.9-bin.zip \
  && mkdir -p /opt/gradle \
  && unzip -d /opt/gradle gradle-2.9-bin.zip \
  && addgroup -S -g 994 jenkins \
  && adduser -S -G jenkins -u 996 jenkins \
  && mkdir -p /opt/mystydies/app \
  && chown -R jenkins:jenkins /opt/mystydies/app


ENV PATH="/opt/gradle/gradle-2.9/bin:${PATH}"

USER jenkins