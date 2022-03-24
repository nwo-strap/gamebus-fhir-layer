#!/bin/bash

if [ $# == 0 ]; then
    echo "Usage: $0 Google_Whistle_engine_PATH"
    echo "Example: $0 healthcare-data-harmonization/mapping_engine/main/main"
    exit
fi

GWC="src/main/gamebus_fhir_r4"

# $1 -input_file_spec=$GWC/example/gb_player.json  -data_harmonization_config_file_spec=$GWC/configurations/player.textproto -output_dir=.
# $1 -input_file_spec=$GWC/example/gb_bp.json  -data_harmonization_config_file_spec=$GWC/configurations/activity.textproto -output_dir=.
# $1 -input_file_spec=$GWC/example/gb_run.json  -data_harmonization_config_file_spec=$GWC/configurations/activity.textproto -output_dir=.

$1 -input_file_spec=$GWC/example/gb_player.json  -data_harmonization_config_file_spec=$GWC/configurations/main.textproto -output_dir=.
$1 -input_file_spec=$GWC/example/gb_bp.json  -data_harmonization_config_file_spec=$GWC/configurations/main.textproto -output_dir=.
$1 -input_file_spec=$GWC/example/gb_run.json  -data_harmonization_config_file_spec=$GWC/configurations/main.textproto -output_dir=.