#!/bin/bash

if [ $# == 0 ]; then
    echo
    echo "This script will build a single-arch image for 'gamebus-fhir-layer' and load it into Docker daemon,"
    echo "so you can run and test it."
    echo "Make sure you have have installed docker and docker buildx."
    echo
    echo "To build and push multi-arch image, please use the script 'build_push_multiarch_image.sh'."
    echo "------------------------------------------------------------------------------------------"
    echo
    echo "Usage: $0 image_version"
    echo "Example: $0 v0.0.3"
    echo
    exit
fi

docker buildx build -t gamebus-fhir-layer:$1 \
    --build-context gw-src=../healthcare-data-harmonization \
    --build-context gw-config-src=../mapping_configs \
    --build-context gamebus-fhir-src=. \
    --load \
    .
