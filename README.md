# GameBus-FHIR Layer

This is a framework to enable GameBus to provide FHIR service.
The framework is built on [HAPI FHIR plain server](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html) and [Google Whistle mapping engine](https://github.com/GoogleCloudPlatform/healthcare-data-harmonization)(GW) with the following architecture:

![GB](image/gb_fhir_layer.png)

## Start the server
The most convenient way to start a GameBus-FHIR server is to run it in container with [docker](https://www.docker.com/),

```bash
# TODO: replace IMAGE with a registered image
# Note: you have to replace GAMEBUS_ENDPOINT with a real url
docker run -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT
```
when the server starts, it'll be served on http://localhost:8080. Check the next section [Usage](#Usage) to see how to send requests to the FHIR server.

## Usage
To use FHIR APIs, we recommend using an API client, e.g. [Postman](https://www.postman.com/), [Hoppscotch](https://hoppscotch.io/), or [httpie](https://httpie.io/).

:bell: Request header `Authorization` is required and you must provide GameBus Bearer token to it when sending requests. See [GameBus guide](https://devdocs.gamebus.eu/#ok-fine-but-how-can-i-make-requests-from-outside-the-app) about how to obtain its token.

-   Test that your server is running by fetching its FHIR `CapabilityStatement`:

    -   <http://localhost:8080/metadata>

    This `CapabilityStatement` lists the available FHIR resources and operations.

-   Try reading back a FHIR resource from your server, e.g.

    -   <http://localhost:8080/Patient/001>
    -   <http://localhost:8080/Observation/001>
    -   <http://localhost:8080/Location/001>

    Note that you need to replace the ID `001` with a valid value (GameBus player ID or activity ID).

-   Try searching FHIR Observations, e.g.

    -   <http://localhost:8080/Observation?code=run,walk>
    -   <http://localhost:8080/Observation?date=gt2022-01-01>
    -   <http://localhost:8080/Observation?code=run,walk&date=gt2022-01-01>

    You could find available search parameters in the `CapabilityStatement`.



## Guide for developers

### Setup development environment

- [Java JDK](https://openjdk.java.net/) (≥17)
- [Apache Maven](https://maven.apache.org/) (≥3.8)
- Google Whistle shared object (see [Guide on building Google Whistle shared object](#Guide-on-building-Google-Whistle-shared-object))


### Start server

To start a local testing GameBus-FHIR server, run the following command:

```bash
# Note that you have to replace "GameBus-Endpoint" with a real URL
mvn -D="jna.library.path=/usr/local/lib" -Dgb.url="GameBus-Endpoint" clean jetty:run
```


### Guide on building Google Whistle shared object

GameBus-FHIR layer use the shared object of Google Whistle mapping engine to conduct data mapping from GameBus structure to FHIR structure.

This guide shows you how to build the shared object from source code:

:bell: Make sure you have installed [the dependencies of GW engine](https://github.com/nwo-strap/healthcare-data-harmonization#details) before the following steps:

```bash
# Download Google Whistle repo
git clone https://github.com/nwo-strap/healthcare-data-harmonization
cd healthcare-data-harmonization/mapping_engine

# The export functions are in `mapping_engine/main/exports.go`,
# update this file based on your needs

# Build shared lib
# The script will generate `libgoogle_whistle.so`(for Linux) or `libgoogle_whistle.dylib`(for MacOS)
# and copy it to `/usr/local/lib` to be used by GameBus-FHIR layer
./build_exports.sh
```

### Guide on developing GameBus-FHIR mapping configurations

The GameBus-FHIR mapping configurations is located in [src/main/gamebus_fhir_r4](src/main/gamebus_fhir_r4), programmed with domain specific language, i.e. [Whistle Data Transformation Language](https://github.com/nwo-strap/healthcare-data-harmonization). This guide shows you how to develop new mapping configurations.

#### 1. Build GW mapping engine by installing its dependencies and run `build_all.sh` (see [detail](https://github.com/nwo-strap/healthcare-data-harmonization#details))

After building, the executable `GW_REPO_LOCAL_PATH/mapping_engine/main/main` should be generated.

#### 2. Clone this repo

```bash
git clone https://github.com/nwo-strap/gamebus-fhir-layer.git
```

#### 3. Update mapping configurations in [src/main/gamebus_fhir_r4](src/main/gamebus_fhir_r4)

Some important materials about Whistle Data Transformation Language

-   [Mini guide on language basics](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/codelab.md)
-   [Language reference](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md)
-   [Builtin functions](https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/builtins.md)

Update the existing mapping configs or develop new mapping configs based on your needs.

#### 4. Test mapping configs

Instead of using this Gamebus-FHIR server to do testing, it's better to directly use GW engine to test the mapping configs.

For example, try the following commands to convert GameBus player to [FHIR patient](https://www.hl7.org/fhir/patient.html):

    cd THIS_REPO_LOCAL_PATH
    GW_REPO_LOCAL_PATH/mapping_engine/main/main -input_file_spec=src/main/gamebus_fhir_r4/example/gb_player.json  -data_harmonization_config_file_spec=src/main/gamebus_fhir_r4/configurations/player.textproto -output_dir=.

The output file is `./gb_player.output.json`.
You can find the reference output files in `src/main/gamebus_fhir_r4/example/output`.

A script is provided to test all examples:

    bash src/main/gamebus_fhir_r4/util/run_mapping.sh GW_REPO_LOCAL_PATH/mapping_engine/main/main

#### 5. Validate generated FHIR resources

To make sure the mapping output conforms to FHIR specification, the [fhir-validator-app](https://github.com/inferno-framework/fhir-validator-app) or its [service](https://inferno.healthit.gov/validator/) can help you on validation.



## Issues and Contributing

If you have questions or find a bug, please report the issue in the
[Github issue channel](https://github.com/nwo-strap/gamebus-fhir-layer/issues).
