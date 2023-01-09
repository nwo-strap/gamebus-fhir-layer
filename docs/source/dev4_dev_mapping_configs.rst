
Develop mapping configs
=======================

The :code:`mapping_configs` `repo`_ contains the mapping rules for data
conversion between GameBus and FHIR (v4). These mapping rules are coded with
a domain specific language, i.e. `Google HCLS Whistle Data Transformation Language`_.

This section will show you how to set up the development environment, how to run mapping using
Google Whistle mapping engine (which is compiled as an executable but not a shared library),
and how to validate mapping results.

Note that the development of mapping configs here is independent of
FHIR server, you don't need to run an FHIR server.


Set up dev environment
----------------------

**Step 1. Build mapping engine**

  This part is similar to the section ":ref:`Build mapping engine<dev2_build_gw_engine:build mapping engine>`",
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

  The executable :code:`healthcare-data-harmonization/mapping_engine/main/main`
  will be generated after building. Check if it exists.


**Step 2. Clone mapping_configs repo**

    .. code-block:: bash

      git clone https://github.com/nwo-strap/mapping_configs.git

  Let's call the path of this clone repo :code:`MAPPING_CONFIG_PATH`, e.g.
  :code:`/home/mapping_configs`.


Now the environment is ready. You can then work with the mapping rules in the
local repo.


Resources for development
-------------------------

To work with mapping rules, you need to be familiar with the Google Whistle language.
Here are some resources for you to get started:

- `Mini guide on language basics`_
- `Language reference`_
- `Builtin functions`_


Run mapping and validate mapping results
----------------------------------------

In this part, we take the :code:`gamebus_fhir_r4` mapping rules in the repo as an example. To
run and validate the mapping rules, you need to do the following:

- Update all :code:`local_path` variables in :code:`mapping_configs/gamebus_fhir_r4/configurations/*.textproto` files.

  If the path of the cloned repo (your :code:`MAPPING_CONFIG_PATH`) is :code:`/mapping_configs`,
  you don't need to do anything; Otherwise, you MUST update all :code:`local_path` with absolute path.

- Run mapping

  Try the following command to convert GameBus player data to FHIR Patient:

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

  Try other mapping configs and input files, you can find reference output files
  in the folder :code:`gamebus_fhir_r4/example/output`.


- Validate mapping results

  To make sure the mapping output conforms to FHIR specification,
  the `fhir-validator-app`_ or its `free service`_ can help you validate the results.
  It is just needed to paste the content of the mapping output to the app or service.



.. _Golang: https://go.dev/dl/
.. _Java JDK: https://openjdk.org/install/
.. _Protobuf Compiler protoc: https://github.com/protocolbuffers/protobuf/releases
.. _Clang: https://clang.llvm.org/get_started.html
.. _adapted Google Whistle codebase: https://github.com/nwo-strap/healthcare-data-harmonization
.. _repo: https://github.com/nwo-strap/mapping_configs
.. _Google HCLS Whistle Data Transformation Language: https://github.com/GoogleCloudPlatform/healthcare-data-harmonization/tree/master/mapping_language
.. _Mini guide on language basics: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/codelab.md
.. _Language reference: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md
.. _Builtin functions: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/builtins.md
.. _fhir-validator-app: https://github.com/inferno-framework/fhir-validator-app
.. _free service: https://inferno.healthit.gov/validator/