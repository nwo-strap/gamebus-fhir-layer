#!/bin/bash

if [ $# == 0 ]; then
    echo
    echo "This script will build the multiarch image for the gamebus-fhir-layer and push it to the docker hub (account 'nlesc')."
    echo "Make sure you have access to docker account 'nlesc' and have installed docker and docker buildx."
    echo "---------------------------------------------"
    echo "Usage: $0 image_version"
    echo "Example: $0 v0.0.3"
    echo
    exit
fi

docker buildx create --name mybuilder01
docker buildx use mybuilder01
docker buildx inspect --bootstrap

docker buildx build --platform linux/arm64,linux/amd64 -t nlesc/gamebus-fhir-layer:$1 -t nlesc/gamebus-fhir-layer:latest \
    --build-context gw-src=../healthcare-data-harmonization \
    --build-context gw-config-src=../mapping_configs \
    --build-context gamebus-fhir-src=. \
    . --push
