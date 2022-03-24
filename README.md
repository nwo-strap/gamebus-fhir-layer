# GameBus-FHIR Layer

This is a framework to enable GameBus to provide FHIR service.
The framework is built on [HAPI FHIR plain server](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html) and [Google Whistle](https://github.com/GoogleCloudPlatform/healthcare-data-harmonization)(GW) with the following architecture:

![GB](image/gb_fhir_layer.png)

## Requirements
- [Java JDK](https://openjdk.java.net/) (>=17)
- [Apache Maven](https://maven.apache.org/) (>=3.8)

## Setup

Before starting the server, you need to configure the server:

1. Create file `config.properties` under folder `src/main/resources`, then add the following content to the file:
  ```
  # Google Whistle config (gwc)
  gwc.player=src/main/gamebus_fhir_r4/configurations/player.textproto
  gwc.activity=src/main/gamebus_fhir_r4/configurations/activity.textproto
  # GameBus config
  gb.url=CHANGE_IT_TO_THE_ACTUAL_GameBus_ENDPOINT
  ```
  You need to change the `gb.url` to actual GameBus endpoint.

2. Create file `token.properties` under the same folder, then add GameBus Bearer tokens to it with the following format:

```
# Add Bearer tokens
# Format: BearerToken=GameBusPlayerID
# Example: 1ac35a48-ee14-4b25-a0eb-c289f8daa421=001
```

## Start server

To start a local testing GameBus-FHIR server, run the following command:
  ```
  mvn jetty:run
  ```

## Usage

The convenient way to try FHIR APIs is to use an API client, e.g. [Postman](https://www.postman.com/) or [Hoppscotch](https://hoppscotch.io/).

Request header `Authorization` is required and you must provide Bearer token to it before sending requests.

- Test that your server is running by fetching its FHIR `CapabilityStatement`:

  - <http://localhost:8080/metadata>

  This `CapabilityStatement` lists the available FHIR resources and operations.

- Try reading back a FHIR resource from your server:
  - http://localhost:8080/Patient/001
  - http://localhost:8080/Observation/001
  - http://localhost:8080/Location/001

  Note that you need to replace the ID `001` with a valid value (GameBus player or activity ID).
- Try searching FHIR Observations:
  - http://localhost:8080/Observation?code=run,walk
  - http://localhost:8080/Observation?date=gt2022-01-01
  - http://localhost:8080/Observation?code=run,walk&date=gt2022-01-01

  You could find available search parameters in the `CapabilityStatement`.



## Guide on developing GameBus-FHIR mapping configurations

The GameBus-FHIR mapping configurations is located in [src/main/gamebus_fhir_r4](src/main/gamebus_fhir_r4), programmed with domain specific language, i.e. [Whistle Data Transformation Language](https://github.com/nwo-strap/healthcare-data-harmonization). This guide shows you how to develop new mapping configurations.


#### 1. Install GW engine by following these [steps](https://github.com/nwo-strap/healthcare-data-harmonization#details)

#### 2. Clone this repo
```bash
git clone https://github.com/nwo-strap/gamebus-fhir-layer.git
```
#### 3. Update mapping configurations in [src/main/gamebus_fhir_r4](src/main/gamebus_fhir_r4)

Some important materials about Whistle Data Transformation Language
- [Mini guide on language basics](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/codelab.md)
- [Language reference](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md)
- [Builtin functions](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/builtins.md)

Update the existing mapping configs or develop new mapping configs based on your needs.

#### 4. Test mapping configs

Instead of using this Gamebus-FHIR server to do testing, it's better to directly use GW engine to test the mapping configs.

For example, try the following commands to convert GameBus player to [FHIR patient](https://www.hl7.org/fhir/patient.html):
```
cd THIS_REPO_LOCAL_PATH
GW_REPO_PATH/mapping_engine/main/main -input_file_spec=src/main/gamebus_fhir_r4/example/gb_player.json  -data_harmonization_config_file_spec=src/main/gamebus_fhir_r4/configurations/main.textproto -output_dir=.
```
The output file is `./gb_player.output.json`.
You can find the reference output files in `src/main/gamebus_fhir_r4/example/output`.

A script is provided to test all examples:
```
bash src/main/gamebus_fhir_r4/util/run_mapping.sh GW_REPO_PATH/mapping_engine/main/main
```

#### 5. Validate generated FHIR resources

To make sure the mapping output conforms to FHIR specification, the [fhir-validator-app](https://github.com/inferno-framework/fhir-validator-app) or its [service](https://inferno.healthit.gov/validator/) can help you on validation.


## Guide on developing Google Whistle engine
Google Whistle engine is used as a shared library in this project ([google_whistle.so](src/main/c/google_whistle.so)) that is compiled from source code available in [this forked repo](https://github.com/nwo-strap/healthcare-data-harmonizationanother).

This guide shows you how to generate the shared library when you change the source code.

To keep the [forked repo](https://github.com/nwo-strap/healthcare-data-harmonizationanother) up to date with the original repo, check the forked repo and click the `Fetch upstream` button to update when available.

Make sure you have installed [the requirements of GW engine](https://github.com/nwo-strap/healthcare-data-harmonization#details) before the following steps:

```bash
# go to your working path
cd YOUR_WORKING_PATH

# download Google Whistle repo
git clone https://github.com/nwo-strap/healthcare-data-harmonization

# build Google Whistle
cd healthcare-data-harmonization
./build_all.sh

# update the export functions in `mapping_engine/main/exports.go`
# based on your needs

# build C lib
cd mapping_engine/main
go build -o google_whistle.so -buildmode=c-shared main.go exports.go

# copy C lib to src/main/c
cp google_whistle.* THIS_REPO_LOCAL_PATH/src/main/c
```

## Issues and Contributing
If you have questions or find a bug, please report the issue in the
[Github issue channel](https://github.com/nwo-strap/gamebus-fhir-layer/issues).