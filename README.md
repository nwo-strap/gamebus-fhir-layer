# FHIR Layer for GameBus

[![github license badge](https://img.shields.io/github/license/nwo-strap/gamebus-fhir-layer)](https://github.com/nwo-strap/gamebus-fhir-layer)
[![Docker Pulls](https://img.shields.io/docker/pulls/nlesc/gamebus-fhir-layer)](https://hub.docker.com/repository/docker/nlesc/gamebus-fhir-layer)
[![Documentation Status](https://readthedocs.org/projects/fhir-layer/badge/?version=latest)](http://fhir-layer.readthedocs.io/?badge=latest)


This is a framework to enable [GameBus](https://blog.gamebus.eu/) platform to
provide [FHIR](http://hl7.org/fhir/) service.

It is built on a technology stack of open source software and consists of two main parts: a mapping engine and a FHIR web server (see diagram below). The two components are integrated into the FHIR layer in order for GameBus to provide FHIR compliant data.

Though it is developed for GameBus platform, it can be easily reused for other healthcare systems with some adaptations to the details.

![FHIR Layer Architecture](docs/source/image/architecture.png)

## Tutorials and documentation

For details on use and development, please see the [tutorials and documentation](https://fhir-layer.readthedocs.io).


## Feedback

If you have questions or find a bug, please report the issue in the
[Github issue channel](https://github.com/nwo-strap/gamebus-fhir-layer/issues).
