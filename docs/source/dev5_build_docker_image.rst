Build docker image
==================

This section shows how to build a docker image of GameBus FHIR layer
from three repos of `healthcare-data-harmonization`_ (mapping engine),
`mapping_configs`_ and `gamebus-fhir-layer`_ (FHIR server).


Requirements
------------

Install the following software

- `docker`_ (≥20.10.14)

After installation, check the version of :code:`buildx`` by running

  .. code-block:: bash

    docker buildx version

Make sure it has a version >=0.8.


Using remote code
-----------------

We can build a docker image of GameBus FHIR layer from the remote GitHub repos:

- Google Whistle mapping engine https://github.com/nwo-strap/healthcare-data-harmonization
- Mapping configs https://github.com/nwo-strap/mapping_configs
- FHIR server https://github.com/nwo-strap/gamebus-fhir-layer

The last repo contains the `Dockerfile`_ used to build the docker image. So first we
need to clone this repo

  .. code-block:: bash

    git clone https://github.com/nwo-strap/gamebus-fhir-layer.git

    # make sure you work in the "gamebus-fhir-layer" repo
    cd gamebus-fhir-layer


Then, build the docker image

  .. code-block:: bash

    docker buildx build --no-cache=true -t gamebus-fhir-layer .

  This command will build a docker image with the name :code:`gambus-fhir-layer`.


By default, the source code from the latest commit of :code:`main` or :code:`master`
branch of each repo will be used to build the docker image.

To use the source code of different versions, you could provide a branch name, commit,
or tag name to the :code:`buildx` command:

  .. code-block:: bash

    docker buildx build --no-cache=true -t gamebus-fhir-layer \
        --build-arg GW_VERSION=[gitBranch_orCommit_orTag] \
        --build-arg GW_CONFIG_VERSION=[gitBranch_orCommit_orTag] \
        --build-arg GAMEBUS_FHIR_VERSION=[gitBranch_orCommit_orTag] \
        --load \
        .

  Replace the :code:`[gitBranch_orCommit_orTag]` with real values.


Using local code
----------------

It's also possible to build a docker image directly from local repos.

First, make sure the three repos locate in the same place. You can clone them
if needed

  .. code-block:: bash

    git clone https://github.com/nwo-strap/healthcare-data-harmonization
    git clone https://github.com/nwo-strap/mapping_configs
    git clone https://github.com/nwo-strap/gamebus-fhir-layer


Then, build the docker image

  .. code-block:: bash

    # make sure you work in the "gamebus-fhir-layer" repo
    cd gambus-fhir-layer

    # build docker image
    docker buildx build --no-cache=true -t gamebus-fhir-layer \
        --build-context gw-src=../healthcare-data-harmonization \
        --build-context gw-config-src=../mapping_configs \
        --build-context gamebus-fhir-src=. \
        --load \
        .

  The :code:`--build-context` argument sets which local repo to use.
  You need to set an absolute path to this argument if your repos are not in the same place.


You can also use the script :code:`build_load_image.sh` to build and load the docker image.

    .. code-block:: bash

      # make sure you work in the "gamebus-fhir-layer" repo
      cd gambus-fhir-layer

      # build docker image
      ./build_load_image.sh v0.0.3


Test Docker image
-----------------

After building the docker image, you can run it with the command:

  .. code-block:: bash

    docker run -p 8080:8080 gamebus-fhir-layer start_fhir_server https://api3-new.gamebus.eu/v2

  Then you can access the FHIR server at http://localhost:8080.


If everything works fine, you can then push the local changes to the remote repos and publish
the multi-arch docker image as described in the next section.


Build and publish multi-arch docker image
-----------------------------------------

We provide a script :code:`build_push_multiarch_image.sh` to build and push the multi-arch docker image.

  .. code-block:: bash

    # make sure you work in the "gamebus-fhir-layer" repo
    cd gambus-fhir-layer

    # build and push multi-arch docker image
    ./build_push_multiarch_image.sh v0.0.3

The script will build the docker images for the platforms :code:`linux/amd64` and :code:`linux/arm64`,
and push them to the Docker Hub with the tag :code:`nlesc/gamebus-fhir-layer:0.0.3`, see https://hub.docker.com/r/nlesc/gamebus-fhir-layer.



.. _healthcare-data-harmonization: https://github.com/nwo-strap/healthcare-data-harmonization
.. _mapping_configs: https://github.com/nwo-strap/mapping_configs
.. _gamebus-fhir-layer: https://github.com/nwo-strap/gamebus-fhir-layer
.. _docker: https://docs.docker.com/engine/install/
.. _Dockerfile: https://github.com/nwo-strap/gamebus-fhir-layer/blob/main/Dockerfile