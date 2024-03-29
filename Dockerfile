# syntax=docker/dockerfile:1.4

#------------------------------------------------------------------------------
# Create a build stage with remote data
#------------------------------------------------------------------------------
FROM alpine AS remote-data
RUN apk add git && \
    rm -rf /var/cache/apk/*
WORKDIR /src

# the version could be git branch, commit or tag
ARG GW_VERSION=master
ARG GW_CONFIG_VERSION=main
ARG GAMEBUS_FHIR_VERSION=main

RUN set -eux && \
    git clone https://github.com/nwo-strap/healthcare-data-harmonization.git && \
        cd healthcare-data-harmonization && \
        git checkout ${GW_VERSION} && cd .. && \
    git clone https://github.com/nwo-strap/mapping_configs.git && \
        cd mapping_configs && \
        git checkout ${GW_CONFIG_VERSION} && cd .. && \
    git clone https://github.com/nwo-strap/gamebus-fhir-layer.git && \
        cd gamebus-fhir-layer && \
        git checkout ${GAMEBUS_FHIR_VERSION}

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
FROM openjdk:23-slim-bullseye AS gw-build

RUN set -eux && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        g++ \
        git \
        unzip \
        wget && \
    apt-get clean

# Set ARGs for Go version, Protoc version, and the target architectures
ARG GO_VERSION=1.18.1
ARG PROTOC_VERSION=3.20.1
ARG TARGETARCH

# Download and install Go and protoc based on the architecture
RUN case "${TARGETARCH}" in \
    'amd64') \
      GOARCH='amd64' \
      PROTOC_ARCH='linux-x86_64' \
      ;; \
    'arm64') \
      GOARCH='arm64' \
      PROTOC_ARCH='linux-aarch_64' \
      ;; \
    *) echo "Unsupported architecture: ${TARGETARCH}" && exit 1 ;; \
    esac && \
    # install Go
    wget "https://golang.org/dl/go${GO_VERSION}.linux-${GOARCH}.tar.gz" && \
    tar -C /usr/local -xzf "go${GO_VERSION}.linux-${GOARCH}.tar.gz" && \
    rm "go${GO_VERSION}.linux-${GOARCH}.tar.gz" && \
    # install protoc
    wget "https://github.com/protocolbuffers/protobuf/releases/download/v${PROTOC_VERSION}/protoc-${PROTOC_VERSION}-${PROTOC_ARCH}.zip" && \
    unzip protoc-${PROTOC_VERSION}-${PROTOC_ARCH}.zip -d /usr/local/protoc && \
    rm protoc-${PROTOC_VERSION}-${PROTOC_ARCH}.zip

ENV PATH="$PATH:/usr/local/go/bin:/usr/local/protoc/bin"

# ================================================================================================


WORKDIR /healthcare-data-harmonization
RUN --mount=target=.,from=gw-src,rw \
    cd mapping_engine && \
    chmod +x ./build_exports.sh && \
    ./build_exports.sh

#------------------------------------------------------------------------------
# Build the target image of GameBus-FHIR layer
#------------------------------------------------------------------------------
FROM openjdk:23-slim-bullseye

# from build stages copy Google Whistle shared object and config files to specific paths
COPY --from=gw-build  /usr/local/lib/libgoogle_whistle.* /usr/local/lib
COPY --from=gw-config-src . /mapping_configs

# install maven
ARG MAVEN_VERSION=3.8.5
RUN set -eux && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        wget && \
    rm -rf /var/lib/apt/lists/* && \
    wget https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    tar -C /usr/local -xzf apache-maven-${MAVEN_VERSION}-bin.tar.gz && \
    rm apache-maven-${MAVEN_VERSION}-bin.tar.gz
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
