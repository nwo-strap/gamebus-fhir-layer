Start FHIR server
=================

GameBus FHIR layer is `open-sourced`_ and available as a `Docker image`_
(id: :code:`nlesc/gamebus-fhir-layer`). The most convenient way to deploy or run
it is using Docker.

This tutorial will show you how to run GameBus FHIR layer using Docker locally.

Run Docker container
--------------------

To start the server, type the command below in a terminal:

.. code-block:: bash

  docker run -p 8080:8080 nlesc/gamebus-fhir-layer start_fhir_server https://api3-new.gamebus.eu/v2

This command runs a Docker container using the :code:`nlesc/gamebus-fhir-layer`
image,

- :code:`-p 8080:8080` binds the container's port :code:`8080` to host machine's port :code:`8080`,
- :code:`start_fhir_server` is the command to start the HAPI FHIR server in container,
- :code:`https://api3-new.gamebus.eu/v2` is the endpoint of GameBus API.

👉 For more details about running a Docker container, see `Docker's doc`_.

When the server starts, it'll be served locally on http://localhost:8080.

Now the FHIR server is ready to use. Before using it, we have to create an
account on GameBus platform and add some test data to GameBus for us to request
later using FHIR REST API, for details see the next section.


.. _open-sourced: https://github.com/nwo-strap/gamebus-fhir-layer
.. _Docker image: https://hub.Docker.com/repository/Docker/nlesc/gamebus-fhir-layer
.. _how to install Docker: https://docs.docker.com/get-docker/
.. _Docker's doc: https://docs.Docker.com/engine/reference/commandline/run/
