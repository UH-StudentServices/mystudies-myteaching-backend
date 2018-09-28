FROM java:openjdk-8-alpine

ARG USER_NAME
ARG USER_ID
ARG GROUP_NAME
ARG GROUP_ID

RUN apk update \
  && apk add bash ca-certificates wget \
  && update-ca-certificates \
  && wget https://services.gradle.org/distributions/gradle-2.9-bin.zip \
  && mkdir -p /opt/gradle \
  && unzip -d /opt/gradle gradle-2.9-bin.zip \
  && addgroup -S -g $GROUP_ID $GROUP_NAME \
  && adduser -S -G $GROUP_NAME -u $USER_ID $USER_NAME \
  && mkdir -p /opt/mystydies/app \
  && chown -R $USER_NAME:$GROUP_NAME /opt/mystydies/app


ENV PATH="/opt/gradle/gradle-2.9/bin:${PATH}"

USER $USER_NAME