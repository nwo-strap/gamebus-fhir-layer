
Develop mapping configs
=======================

The `mapping_configs repo`_ contains the mapping rules for transforming data from GameBus format to
FHIR (R4) format.
The mapping rules are implemented using the domain specific language `Google Whistle language`_.

This section will show you how to develop new mapping rules.

.. note::

    You don't need to run a FHIR server to develop mapping rules.


Set up dev environment
----------------------

**Step 1. Build mapping engine**

  This step is similar to the section ":ref:`Build mapping engine<dev2_build_gw_engine:build mapping engine>`",
  but it is not going to build a C shared library. Instead, we just need to build
  an executable from the Go source code.

  - First, install the following dependencies

    1. `Golang`_ (>=1.7)
    2. `Java JDK`_ (>= 8)
    3. `Protobuf Compiler protoc`_ (>= 3.11.4)
    4. `Clang`_ (>= 11.0.1-2)


  - Second, clone mapping engine codebase

    .. code-block:: bash

        git clone https://github.com/nwo-strap/healthcare-data-harmonization

  - Lastly, build it

    .. code-block:: bash

        cd healthcare-data-harmonization
        ./build_all.sh

  The executable :code:`healthcare-data-harmonization/mapping_engine/main/main` will be generated.
  Check if it exists.


**Step 2. Clone mapping_configs repo**

    .. code-block:: bash

      git clone https://github.com/nwo-strap/mapping_configs.git

  Let's call the path of this clone repo :code:`MAPPING_CONFIG_PATH`, e.g.
  :code:`/home/mapping_configs`.


Develop new mapping rules
-------------------------

The `mapping_configs repo`_ has a structure like this:

.. code-block:: bash

    mapping_configs
    ├── LICENSE
    ├── README.md
    └── gamebus_fhir_r4   # mapping rules for GameBus to FHIR R4
        ├── README.md
        ├── projector_library
        │   ├── datatypes.wstl  # You can find what GameBus data types are supported in "Property_ValueX" function
        │   └── resources.wstl  # You can find what FHIR resources are supported here
        ├── code_harmonization
        │   ├── Activity2Resource.harmonization.json # You can find what GameBus activity types are supported here
        │   ├── Gender.harmonization.json
        │   ├── ObservationCategory.harmonization.json
        │   ├── ObservationCode.harmonization.json
        │   ├── ObservationComponentCode.harmonization.json # You can find what GameBus property types are supported here
        │   └── Unit.harmonization.json # You can find what GameBus units are supported here
        ├── configurations
        │   ├── activity.textproto
        │   ├── activity.wstl
        │   ├── player.textproto
        │   └── player.wstl
        └── example
            ├── gb_bp.json
            ├── gb_location.json
            ├── gb_player.json
            ├── gb_run.json
            └── output
                ├── gb_bp.output.json
                ├── gb_location.output.json
                ├── gb_player.output.json
                └── gb_run.output.json

Current mapping rules support the following:

- FHIR (R4) resources:
    - :code:`Patient`, :code:`Observation` and :code:`Location`
    - Better to double check the file :code:`projector_library/resources.wstl` for all supported resources.
- GameBus data types (i.e. :code:`property.inputType`):
    - :code:`INT`, :code:`STRING`, :code:`BOOL`, :code:`DOUBLE`, :code:`DISTANCE`, :code:`DURATION`, :code:`COORDINATE` and :code:`DATE`.
    - Better to double check the function :code:`Property_ValueX` in the file :code:`projector_library/datatypes.wstl` for all supported data types.
- GameBus activity types (i.e. :code:`gameDescriptor.translationKey`):
    - Check the file :code:`Activity2Resource.harmonization.json`
- GameBus property types (i.e. :code:`property.translationKey`):
    - Check the file :code:`ObservationComponentCode.harmonization.json`


Depending on whether you need new FHIR resources or not, the development process will be different.
We will discuss the two different scenarios in the following sections.

To work with mapping rules, you need to be familiar with the Google Whistle language.
Here are some resources for you to get started:

- `Mini guide on language basics`_
- `Language reference`_
- `Builtin functions`_

Scenario 1: Using existing FHIR resources
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In this scenario, you want to transform GameBus data to FHIR resources that are already supported,
e.g. :code:`Patient`, :code:`Observation` and :code:`Location`.

The workflow of developing new mapping rules is as follows:

1. Check if the data types of the GameBus data are supported. If not, you need to update the function `Property_ValueX` in the file :code:`projector_library/datatypes.wstl`.
2. Use the `excel-to-fhir`_ tool to generate the harmonization files for new GameBus activities, properties and/or units. Then you need to update the corresponding harmonization files in the folder :code:`code_harmonization`.

That is all you need to do to develop new mapping rules in this scenario. Go next sections to see how to run and validate the mapping rules.

Scenario 2: Requiring new FHIR resources
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

In this scenario, you need to transform GameBus data to FHIR resources than the current ones, e.g. `Device`_.

Here is the workflow of developing new mapping rules:

1. Check if the data types of the GameBus data are supported. If not, you need to update the function `Property_ValueX` in the file :code:`projector_library/datatypes.wstl`.
2. Use the `excel-to-fhir`_ tool to generate the harmonization files for new GameBus activities, properties and/or units. Then you need to update the corresponding harmonization files in the folder :code:`code_harmonization`.
3. Update the file :code:`projector_library/resources.wstl` to add the new FHIR resource. You may also need to update the file :code:`projector_library/datatypes.wstl` to add some new functions.


After finishing the above steps, you can run and validate the mapping rules as described in the next sections.


.. NOTE::

    When you add new FHIR resources in mapping rules, you also need to update the FHIR server to support
    the new resources to make the whole system work properly.
    For more details, check :ref:`next part<dev4_dev_hapi_fhir:Develop FHIR server>`.

Run the mapping
---------------

To run the mapping rules, you need to do the following:

- Update all :code:`local_path` variables in :code:`mapping_configs/gamebus_fhir_r4/configurations/*.textproto` files.

  If the path of the cloned repo (your :code:`MAPPING_CONFIG_PATH`) is :code:`/mapping_configs`,
  you don't need to do anything; Otherwise, you MUST update all :code:`local_path` with the absolute path.

- Run mapping

  Use the following commands to convert GameBus player data to FHIR :code:`Patient`:

    .. code-block:: bash

      cd mapping_configs

      [BASE_PATH]/healthcare-data-harmonization/mapping_engine/main/main \
          -data_harmonization_config_file_spec=./gamebus_fhir_r4/configurations/player.textproto \
          -input_file_spec=./gamebus_fhir_r4/example/gb_player.json
          -output_dir=.

    * The :code:`[BASE_PATH]` is the path to the mapping engine repo, replace it with real value.
    * :code:`-data_harmonization_config_file_spec` sets which mapping config file to use.
      Take a look at all config files and try others for different types of input.
    * :code:`-input_file_spec` sets the path to the input JSON file.
    * :code:`-output_dir` sets the path to the output directory,

  The output file is :code:`./gb_player.output.json`, which is named based on
  the name of the input file.


  Use the following command to convert GameBus :code:`RUN` data to FHIR :code:`Observation`:

    .. code-block:: bash

      cd mapping_configs

      [BASE_PATH]/healthcare-data-harmonization/mapping_engine/main/main \
          -data_harmonization_config_file_spec=./gamebus_fhir_r4/configurations/activity.textproto \
          -input_file_spec=./gamebus_fhir_r4/example/gb_run.json
          -output_dir=.

    The output file is :code:`./gb_run.output.json`.

  Try other GameBus example data, you can find reference output files in the folder :code:`gamebus_fhir_r4/example/output`.


Validate FHIR data
------------------

To make sure the mapping output conforms to FHIR (R4) specification, you can use the `fhir-validator-app`_ or
its `free service`_ to validate the results. What you need to do is just to paste the content of the mapping output to the app or service.
The app or service will tell you if the content is valid or not; if not valid, it will give you the error message.


.. _Golang: https://go.dev/dl/
.. _Java JDK: https://openjdk.org/install/
.. _Protobuf Compiler protoc: https://github.com/protocolbuffers/protobuf/releases
.. _Clang: https://clang.llvm.org/get_started.html
.. _adapted Google Whistle codebase: https://github.com/nwo-strap/healthcare-data-harmonization
.. _mapping_configs repo: https://github.com/nwo-strap/mapping_configs
.. _Google Whistle language: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md
.. _Mini guide on language basics: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/codelab.md
.. _Language reference: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md
.. _Builtin functions: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/builtins.md
.. _fhir-validator-app: https://github.com/inferno-framework/fhir-validator-app
.. _free service: https://inferno.healthit.gov/validator/
.. _excel-to-fhir: https://github.com/nwo-strap/excel-to-fhir
.. _Device: https://hl7.org/fhir/R4/device.html