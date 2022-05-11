# GameBus-FHIR Layer

This is a framework to enable GameBus to provide FHIR service.
The framework is built on [HAPI FHIR plain server](https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html) and [Google Whistle mapping engine](https://github.com/GoogleCloudPlatform/healthcare-data-harmonization)(GW) with the following architecture:

![GB](image/gb_fhir_layer.png)

## Start the server
The most convenient way to start a GameBus-FHIR server is to run it in container with [docker](https://www.docker.com/),

```bash
# TODO: replace IMAGE with a registered image
# Note: you have to replace GAMEBUS_ENDPOINT with a real url
docker run -it -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT
```
when the server starts, it'll be served on http://localhost:8080. Check the next section [Usage](#Usage) to see how to send requests to the FHIR server.


You could also use docker volume to store the [mapping_configs](https://github.com/nwo-strap/mapping_configs). When mounted to container, it will override the existing mapping configs in the image. This will allow you to update mapping configs even when the container is running. Start a server with volume:

```
docker run -it -v YOUR_VOLUME:/mapping_configs -p 8080:8080 IMAGE start_fhir_server GAMEBUS_ENDPOINT
```

## Usage
To use FHIR APIs, we recommend using an API client, e.g. [Postman](https://www.postman.com/), [Hoppscotch](https://hoppscotch.io/), or [httpie](https://httpie.io/). Postman collections for testing GameBus-FHIR APIs is [available](https://github.com/nwo-strap/postman-collections). You could also do testing as described below.

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



## Guide on development

### Setup development environment

- [Java JDK](https://openjdk.java.net/) (≥17)
- [Apache Maven](https://maven.apache.org/) (≥3.8)
- Google Whistle shared object (see [Guide on building Google Whistle shared object](#Guide-on-building-Google-Whistle-shared-object))
- [Mapping configs](https://github.com/nwo-strap/mapping_configs)
    - You need to [update the `local_path` in `mapping_configs/gamebus_fhir_r4/configurations/*.textproto` files](https://github.com/nwo-strap/mapping_configs#41-update-the-local_path-in-gamebus_fhir_r4configurationstextproto-files)



### Start local server

To start a local testing GameBus-FHIR server, run the following command:

```bash
# NB: You have to replace "GameBus-ENDPOINT" and "mapping_configs_ABSOLUTE_PATH" with real value

mvn -D="jna.library.path=/usr/local/lib" \
    -Dgb.url="GameBus-ENDPOINT" \
    -Dgwc.player="mapping_configs_ABSOLUTE_PATH/gamebus_fhir_r4/configurations/player.textproto" \
    -Dgwc.activity="mapping_configs_ABSOLUTE_PATH/gamebus_fhir_r4/configurations/activity.textproto" \
    jetty:run
```

## Guide on building docker image

### Requirements
- [docker](https://docs.docker.com/engine/install/) (≥20.10.14)

### Build image

#### Use remote code from github repos

```
docker buildx build --no-cache=true -t gb-fhir-server .
```
it will automatically clone the code from the three repos
- GW mapping engine https://github.com/nwo-strap/healthcare-data-harmonization
- Mapping cnofigs https://github.com/nwo-strap/mapping_configs
- GameBus-FHIR layer https://github.com/nwo-strap/gamebus-fhir-layer


#### Use your local code
```
# it assumes you have cloned the three repos to a same place,
# and you run the command below in the clone of this repo

docker buildx build --no-cache=true -t gb-fhir-server \
    --build-context gw-src=../healthcare-data-harmonization \
    --build-context gw-config-src=../mapping_configs \
    --build-context gamebus-fhir-src=. .
```
This way allows you build the image from locally updated code.

## Guide on building Google Whistle shared object

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



## Issues and Contributing

If you have questions or find a bug, please report the issue in the
[Github issue channel](https://github.com/nwo-strap/gamebus-fhir-layer/issues).
