Build docker image
==================

This section shows how to build a docker image of GameBus FHIR layer
from three repos of `healthcare-data-harmonization`_ (mapping engine),
`mapping_config`_ and `gamebus-fhir-layer`_ (FHIR server).


Requirement
-----------

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

To use the source code of different versions, you could provide a branch name, commit
, or tag name to the :code:`buildx` command:

  .. code-block:: bash

    docker buildx build --no-cache=true -t gamebus-fhir-layer \
        --build-arg GW_VERSION=[gitBranch_orCommit_orTag] \
        --build-arg GW_CONFIG_VERSION=[gitBranch_orCommit_orTag] \
        --build-arg GAMEBUS_FHIR_VERSION=[gitBranch_orCommit_orTag] \
        .

  Replace the :code:`[gitBranch_orCommit_orTag]` with real values.


Using local code
----------------

It's also possible to build a docker image directly from local repos.
It helps a lot to test new code before pushing them to remote.

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
        .

  The :code:`--build-context` argument sets which local repo to use.
  You need to set an absolute path to this argument if your repos are not in the same place.


.. _healthcare-data-harmonization: https://github.com/nwo-strap/healthcare-data-harmonization
.. _mapping_config: https://github.com/nwo-strap/mapping_configs
.. _gamebus-fhir-layer: https://github.com/nwo-strap/gamebus-fhir-layer
.. _docker: https://docs.docker.com/engine/install/
.. _Dockerfile: https://github.com/nwo-strap/gamebus-fhir-layer/blob/main/Dockerfile
