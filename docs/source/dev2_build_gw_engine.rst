
Build mapping engine
====================

This section will show you how to build `Google Whistle (GW)`_ mapping engine
(C shared library) for GameBus FHIR layer.

Build mapping engine shared library
-----------------------------------

**Step 1, install the following dependencies**

1. `Golang`_ (>=1.7)
2. `Java JDK`_ (>= 8)
3. `Protobuf Compiler protoc`_ (>= 3.11.4)
4. `Clang`_ (>= 11.0.1-2)


**Step 2, download** `adapted Google Whistle codebase`_.
  See :ref:`subsection below<dev2_build_gw_engine:google whistle codebase>` for more info about the codebase.

  .. code-block:: bash

    git clone https://github.com/nwo-strap/healthcare-data-harmonization
    cd healthcare-data-harmonization/mapping_engine


**Step 3, build C shared library**

  .. code-block:: bash

    ./build_exports.sh


  This script will generate C shared library :code:`libgoogle_whistle.so` (for Linux) or
  :code:`libgoogle_whistle.dylib` (for macOS) and create a corresponding symbolic
  link in the path :code:`/usr/local/lib`. The GameBus FHIR layer will seek the
  mapping engine library in this path.


Google Whistle codebase
-----------------------

Compared with `original Google Whistle codebase`_, the `codebase used above`_
is a forked codebase and updated with three new scripts:

- `mapping_engine/build_exports.sh`_
   It's a helper BASH script to generate Go code from protobuf files and then compile
   Go code (e.g. export functions) to C shared library.

- `mapping_engine/main/exports.go`_
   The export function :code:`RunMapping` is defined in this Go file,
   which converts JSON string of one structure to another.

   This Go file can be updated to add other export functions.

- `Dockerfile`_
  The dockerfile to build a docker image of the mapping engine.


.. _Google Whistle (GW): https://github.com/nwo-strap/healthcare-data-harmonization
.. _Golang: https://go.dev/dl/
.. _Java JDK: https://openjdk.org/install/
.. _Protobuf Compiler protoc: https://github.com/protocolbuffers/protobuf/releases
.. _Clang: https://clang.llvm.org/get_started.html
.. _adapted Google Whistle codebase: https://github.com/nwo-strap/healthcare-data-harmonization
.. _original Google Whistle codebase: https://github.com/GoogleCloudPlatform/healthcare-data-harmonization
.. _mapping_engine/build_exports.sh: https://github.com/nwo-strap/healthcare-data-harmonization/blob/453b9dc60cb58973a72466d4273355d02774820d/mapping_engine/build_exports.sh
.. _mapping_engine/main/exports.go: https://github.com/nwo-strap/healthcare-data-harmonization/blob/453b9dc60cb58973a72466d4273355d02774820d/mapping_engine/main/exports.go
.. _Dockerfile: https://github.com/nwo-strap/healthcare-data-harmonization/blob/453b9dc60cb58973a72466d4273355d02774820d/Dockerfile
.. _codebase used above: https://github.com/nwo-strap/healthcare-data-harmonization