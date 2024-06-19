
Introduction
============

GameBus FHIR layer is built on a tech stack of open-source softwares, comprising two major components:

- **Mapping engine**

  Mapping engine is a component to convert data from one format to another,
  e.g. from GameBus JSON data to FHIR JSON data.

  `Google HCLS Data Harmonization`_ is used as the mapping engine of GameBus FHIR layer.
  The engine supports transformation between any two formats or schemas by configuring
  mapping rules (or mapping configs).

  The mapping rules can be implemented with the `Whistle Data Transformation Language`_, which will
  be automatically transpiled to `protobuf`_.

  In this documentation, we use `Google Whistle` or `GW` to refer to this mapping engine or language.

- **FHIR web server**

  FHIR web server is the server to provide the capabilities of FHIR REST API.

  `HAPI FHIR framework`_ is used to add these capabilities to GameBus FHIR layer.
  With this framework, a `HAPI FHIR plain server`_ was created and some
  resource providers were defined to serve up FHIR resources, e.g. Patient and Observation.

  The FHIR server has two functionalities in GameBus FHIR layer:

  - It forwards the user's HTTP request to GameBus REST API after transforming the
    request for FHIR REST API to the request for GameBus API.
  - It gets FHIR-compliant data from mapping engine and sends these data to the user
    as HTTP response through FHIR REST API.

.. image:: image/architecture.png
  :width: 800
  :alt: GameBus FHIR Layer

One of the advantages of this tech stack is that it does not change any code or
schema of GameBus platform, but just add one more layer on the existing platform
to add the capabilities of FHIR REST API. Moreover, though this FHIR layer is developed for
GameBus platform, the tech stack can be easily applied to other healthcare
platforms to enable FHIR service.


.. _Google HCLS Data Harmonization: https://github.com/GoogleCloudPlatform/healthcare-data-harmonization
.. _protobuf: https://developers.google.com/protocol-buffers/docs/overview
.. _Whistle Data Transformation Language: https://github.com/nwo-strap/healthcare-data-harmonization/blob/master/mapping_language/doc/reference.md
.. _HAPI FHIR framework: https://hapifhir.io/hapi-fhir/
.. _HAPI FHIR plain server: https://hapifhir.io/hapi-fhir/docs/server_plain/introduction.html