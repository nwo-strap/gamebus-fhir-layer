# TODO: change the base image to a registered image e.g. nlesc/fhir-mapping-engine
FROM fhir-mapping-engine

ENV MAVEN_VERSION=3.8.5
RUN set -eux && \
    apt-get update && \
    apt-get install -y --no-install-recommends \
        wget && \
    rm -rf /var/lib/apt/lists/* && \
    # Install maven
    wget https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    tar -C /usr/local -xzf apache-maven-$MAVEN_VERSION-bin.tar.gz && \
    rm apache-maven-$MAVEN_VERSION-bin.tar.gz
ENV PATH="/usr/local/apache-maven-$MAVEN_VERSION/bin:$PATH"

WORKDIR /app
COPY . .

# Make start server script executable
RUN set -eux && \
    chmod +x start_fhir_server.sh && \
    ln -s $(pwd)/start_fhir_server.sh /usr/local/bin/start_fhir_server

# Create non-root user "fhir"
RUN set -eux && \
    groupadd -r -g 1000 fhir && \
    useradd -mr -g fhir -u 1000 -s /bin/bash fhir && \
    chown fhir:fhir /app
USER fhir

EXPOSE 8080
CMD ["echo", "Usage: docker run -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT"]