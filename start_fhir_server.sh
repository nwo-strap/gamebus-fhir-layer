#!/bin/bash

if [ $# != 1 ]; then
    echo "Usage: $0 GameBus_Endpoint"
    echo
    exit
else
    mvn -D="jna.library.path=/usr/local/lib" -Dgb.url=$1 jetty:run
fi