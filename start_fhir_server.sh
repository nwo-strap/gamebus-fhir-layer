#!/bin/bash

if [ $# != 1 ]; then
    echo "Usage: $0 GameBus_Endpoint"
    echo
    exit
else
    mvn -D="jna.library.path=/usr/local/lib" \
        -Dgb.url=$1 \
        -Dgwc.player=/mapping_configs/gamebus_fhir_r4/configurations/player.textproto \
        -Dgwc.activity=/mapping_configs/gamebus_fhir_r4/configurations/activity.textproto \
        jetty:run
fi