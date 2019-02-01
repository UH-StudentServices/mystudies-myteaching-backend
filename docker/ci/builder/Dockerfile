FROM openjdk:11-jdk-slim

ARG USER_NAME
ARG USER_ID
ARG GROUP_NAME
ARG GROUP_ID

RUN apt-get update \
  && apt-get install -y --no-install-recommends bash ca-certificates wget git openssh-client \
  && rm -rf /var/lib/apt/lists/* \
  && update-ca-certificates \
  && wget https://services.gradle.org/distributions/gradle-5.1.1-bin.zip \
  && mkdir -p /opt/gradle \
  && unzip -d /opt/gradle gradle-5.1.1-bin.zip \
  && addgroup --system --gid $GROUP_ID $GROUP_NAME \
  && useradd --system -g $GROUP_NAME --uid $USER_ID $USER_NAME \
  && mkdir -p /opt/mystudies \
  && mkdir -p /home/$USER_NAME/.gradle \
  && mkdir -p /home/$USER_NAME/.m2 \
  && mkdir -p /home/$USER_NAME/.sonar \
  && chown -R $USER_NAME:$GROUP_NAME /opt/mystudies \
  && chown -R $USER_NAME:$GROUP_NAME /home/$USER_NAME/.gradle \
  && chown -R $USER_NAME:$GROUP_NAME /home/$USER_NAME/.m2 \
  && chown -R $USER_NAME:$GROUP_NAME /home/$USER_NAME/.sonar

ENV PATH="/opt/gradle/gradle-5.1.1/bin:${PATH}"

USER $USER_NAME

WORKDIR /opt/mystudies

ENTRYPOINT ["gradle"]
