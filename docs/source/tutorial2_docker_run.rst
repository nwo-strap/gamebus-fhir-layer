Start a GameBus FHIR server
===========================

GameBus FHIR layer is `open-source`_ software and released as `Docker images`_
(id: :code:`nlesc/gamebus-fhir-layer`).

To start a GameBus FHIR server with Docker, type the following command in a terminal:

.. code-block:: bash

  docker run -p 8080:8080 nlesc/gamebus-fhir-layer start_fhir_server https://api3-new.gamebus.eu/v2

This command runs a `Docker container`_ using the :code:`nlesc/gamebus-fhir-layer`
image,

- :code:`-p 8080:8080` binds the container's port :code:`8080` to host machine's port :code:`8080`,
- :code:`start_fhir_server` is the command to start the HAPI FHIR server in container,
- :code:`https://api3-new.gamebus.eu/v2` is the endpoint of GameBus API.

When the server starts, it'll be served locally on http://localhost:8080.

Now, the FHIR server is ready. Before using it, we have to create an
account on GameBus platform and add some example data to GameBus for us to request
later using FHIR REST API, for details see the next section.


.. _open-source: https://github.com/nwo-strap/gamebus-fhir-layer
.. _Docker images: https://hub.Docker.com/repository/Docker/nlesc/gamebus-fhir-layer
.. _how to install Docker: https://docs.docker.com/get-docker/
.. _Docker's doc: https://docs.Docker.com/engine/reference/commandline/run/
.. _Docker container: https://docs.docker.com/reference/cli/docker/container/run/
