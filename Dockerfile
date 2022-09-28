# syntax=docker/dockerfile:1.4

#------------------------------------------------------------------------------
# Create a build stage with remote data
#------------------------------------------------------------------------------
FROM alpine as remote-data
RUN apk add git
WORKDIR /src

# the version could be git branch, commit or tag
ARG GW_VERSION=master
ARG GW_CONFIG_VERSION=main
ARG GAMEBUS_FHIR_VERSION=main

RUN set -eux && \
    git clone https://github.com/nwo-strap/healthcare-data-harmonization.git && \
        cd healthcare-data-harmonization && \
        git checkout $GW_VERSION && cd .. && \
    git clone https://github.com/nwo-strap/mapping_configs.git && \
        cd mapping_configs && \
        git checkout $GW_CONFIG_VERSION && cd .. && \
    git clone https://github.com/nwo-strap/gamebus-fhir-layer.git && \
        cd gamebus-fhir-layer && \
        git checkout $GAMEBUS_FHIR_VERSION

#------------------------------------------------------------------------------
# Create helper stages
# Use named build context `--build-context [name]=..` to override these stages
#------------------------------------------------------------------------------
FROM scratch AS gw-src
COPY --from=remote-data /src/healthcare-data-harmonization /

FROM scratch AS gw-config-src
COPY --from=remote-data /src/mapping_configs /

FROM scratch AS gamebus-fhir-src
COPY --from=remote-data /src/gamebus-fhir-layer /

#------------------------------------------------------------------------------
# Build the shared object of Google Whistle mapping engine
#------------------------------------------------------------------------------
FROM openjdk:18-slim-bullseye AS gw-build

RUN set -eux && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        g++ \
        git \
        unzip \
        wget && \
    # install Go and protoc
    wget https://golang.org/dl/go1.18.1.linux-amd64.tar.gz && \
    tar -C /usr/local -xzf go1.18.1.linux-amd64.tar.gz && \
    wget https://github.com/protocolbuffers/protobuf/releases/download/v3.20.1/protoc-3.20.1-linux-x86_64.zip && \
    mkdir /usr/local/protoc && \
    unzip protoc-3.20.1-linux-x86_64.zip -d /usr/local/protoc
ENV PATH="$PATH:/usr/local/go/bin:/usr/local/protoc/bin"

WORKDIR /healthcare-data-harmonization
RUN --mount=target=.,from=gw-src,rw \
    cd mapping_engine && \
    chmod +x ./build_exports.sh && \
    ./build_exports.sh

#------------------------------------------------------------------------------
# Build the target image of GameBus-FHIR layer
#------------------------------------------------------------------------------
FROM openjdk:18-slim-bullseye

# from build stages copy Google Whistle shared object and config files to specific paths
COPY --from=gw-build  /usr/local/lib/libgoogle_whistle.* /usr/local/lib
COPY --from=gw-config-src . /mapping_configs

# install maven
ENV MAVEN_VERSION=3.8.5
RUN set -eux && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        wget && \
    rm -rf /var/lib/apt/lists/* && \
    wget https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    tar -C /usr/local -xzf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    rm apache-maven-$MAVEN_VERSION-bin.tar.gz
ENV PATH="/usr/local/apache-maven-$MAVEN_VERSION/bin:$PATH"

# from build stage copy GameBus-FHIR layer code
WORKDIR /app
COPY --from=gamebus-fhir-src . .
RUN set -eux && \
    chmod +x start_fhir_server.sh && \
    ln -s $(pwd)/start_fhir_server.sh /usr/local/bin/start_fhir_server

# create non-root user "fhir"
RUN set -eux && \
    groupadd -r -g 1000 fhir && \
    useradd -mr -g fhir -u 1000 -s /bin/bash fhir && \
    chown fhir:fhir /app
USER fhir

EXPOSE 8080
CMD ["echo", "Usage: docker run -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT"]