Develop FHIR server
===================

This section guides you on how to set up the development environment for GameBus FHIR
layer and how to start a local test FHIR server.


Set up dev environment
----------------------

**Step 1, install the following dependencies**

1. `Java JDK`_ (>=17)
2. `Apache Maven`_ (>=3.8)

**Step 2, build mapping engine**
  Check :ref:`this section<dev2_build_gw_engine:Build mapping engine shared library>`
  to build Google Whistle mapping engine (C shared library), which is used by
  FHIR server to convert data.

**Step 3, set mapping configs**

  Mapping configs are the rules used by Google Whistle mapping engine to convert
  data from one format to another. The repo of mapping configs has pre-defined rules
  for data conversion between GameBus and FHIR. It should be set properly to be used
  in GameBus FHIR layer, see the steps below:

  First, clone mapping configs repo

    .. code-block:: bash

      git clone https://github.com/nwo-strap/mapping_configs.git

    Let's assume the path of this clone is :code:`MAPPING_CONFIG_PATH`, e.g.
    :code:`/home/mapping_configs`.

  Then, update all :code:`local_path` variables in :code:`gamebus_fhir_r4/configurations/*.textproto` files.

    If the path of the cloned repo (your :code:`MAPPING_CONFIG_PATH`) is :code:`/mapping_configs`,
    you don't need to do anything; Otherwise, you MUST update all :code:`local_path` with absolute path.

**Step 4, clone source code**

    .. code-block:: bash

      git clone https://github.com/nwo-strap/gamebus-fhir-layer.git

    The :code:`gamebus-fhir-layer` repo contains the implementation of the FHIR
    server by taking advantage of HAPI FHIR framework.


Resource for developing FHIR server
-----------------------------------

Now it's ready for further development of FHIR server based on HAPI FHIR framework,
e.g. adding or changing FHIR resources or operations.

HAPI FHIR website has `great documentation`_ for developers to build an FHIR server.
Also, you could check existing code in :code:`gamebus-fhir-layer` repo to get
a sense of how the HAPI FHIR framework works.


Start a local test server
-------------------------

Start a local FHIR server to test new functionalities:

  .. code-block:: bash

    # make sure you are working in the gamebus-fhir-layer repo
    cd gamebus-fhir-layer

    # Replace "[GAMEBUS_API_URL]" and "[mapping_configs_ABSOLUTE_PATH]" with real values
    mvn -D="jna.library.path=/usr/local/lib" \
        -Dgb.url="[GAMEBUS_API_URL]" \
        -Dgwc.player="[mapping_configs_ABSOLUTE_PATH]/gamebus_fhir_r4/configurations/player.textproto" \
        -Dgwc.activity="[mapping_configs_ABSOLUTE_PATH]/gamebus_fhir_r4/configurations/activity.textproto" \
        jetty:run

- :code:`-D="jna.library.path=/usr/local/lib"` sets the path of mapping engine shared library
- :code:`-Dgb.url="[GAMEBUS_API_URL]` sets the URL of GameBus REST API, which is
  https://api3-new.gamebus.eu/v2.
- :code:`-Dgwc.player="[mapping_configs_ABSOLUTE_PATH]/gamebus_fhir_r4/configurations/player.textproto"`
  sets the mapping config for GameBus player data. Replace the :code:`[mapping_configs_ABSOLUTE_PATH]`
  with the real value of the absolute path of the mapping configs repo (see step 3 above).
- :code:`-Dgwc.activity="[mapping_configs_ABSOLUTE_PATH]/gamebus_fhir_r4/configurations/activity.textproto"`
  sets the mapping config for GameBus activity data.

By default, the server will be served at the base "http://localhost:8080".

To test the FHIR server, it's required to add test data to GameBus,
check :ref:`the tutorial<tutorial3_gamebus:gamebus>` to see how to add data to
GameBus.

Then you can send HTTP requests to test the new functionalities of the FHIR server.
See this tutorial about :ref:`how to request on FHIR REST API<tutorial4_fhir_api:request on fhir api>`.



.. _Java JDK: https://openjdk.org/
.. _Apache Maven: https://maven.apache.org/
.. _HAPI FHIR's doc: https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html

.. _great documentation: https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html
