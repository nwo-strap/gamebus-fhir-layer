# TODO: change the base image to a registered image e.g. nlesc/fhir-mapping-engine
FROM fhir-mapping-engine

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        wget && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY . .

RUN chmod +x start_fhir_server.sh && \
    ln -s $(pwd)/start_fhir_server.sh /usr/local/bin/start_fhir_server && \
    # install maven
    wget https://dlcdn.apache.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz && \
    tar -C /usr/local -xzf apache-maven-3.8.5-bin.tar.gz && \
    rm apache-maven-3.8.5-bin.tar.gz

ENV PATH="$PATH:/usr/local/apache-maven-3.8.5/bin"

EXPOSE 8080

CMD ["echo", "Usage: docker run -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT"]